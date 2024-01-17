package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {

    private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

    private static final String TMP_DIR = "";

    private static final String LOCAL_ZIP_FILE_NAME = "submission.zip";

    //FIX ME
    private static final String SUBMISSION_ATTACHMENTS_BUCKET_NAME = "gap-devs";
    //regex for any special character that are not allowed in window os : <, >, ", /, \, |, ?, or *
    private static final String SPECIAL_CHARACTER_REGEX = "[<>\"\\/|?*\\\\]";

    public static final Integer LONG_FILE_NAME_LENGTH = 50; //50 characters may be too strict but can revisit if required
    private static final AmazonS3 client =  AmazonS3ClientBuilder.standard()
            .withRegion(Regions.EU_WEST_2)
            .build();


    public static ByteArrayOutputStream createSubmissionZip(final String applicationId,
                                 final String submissionId, final byte[] odtContent) throws IOException {
        final List<String> submissionAttachmentFileNames = getSubmissionAttachmentFileNames(client, applicationId,
                submissionId);
        for (String fileName : submissionAttachmentFileNames) {
            downloadFile(fileName);
        }

        final List<String> fileNamesToZIP = new ArrayList<>(submissionAttachmentFileNames);

        ByteArrayOutputStream zip = zipFiles(fileNamesToZIP, applicationId, submissionId, odtContent);
        logger.info("Zip file created");
        return zip;
    }

    public static String uploadZip(final Submission submission, final String zipFilename) {
        try {
            final String objectKey = submission.getGapId() + "/" + zipFilename + ".zip";
            client.putObject(System.getenv("SUBMISSION_EXPORTS_BUCKET_NAME"), objectKey,
                    new File(TMP_DIR + LOCAL_ZIP_FILE_NAME));
            logger.info("Zip file uploaded to S3");
            return objectKey;
        } catch (Exception e) {
            logger.error("Could not upload to S3", e);
            throw e;
        }
    }

    public static List<String> getSubmissionAttachmentFileNames(final AmazonS3 s3Client,
                                                                final String applicationId,
                                                                final String submissionId) {
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(SUBMISSION_ATTACHMENTS_BUCKET_NAME)
                .withPrefix(applicationId + "/" + submissionId);
        final ListObjectsV2Result listing = s3Client.listObjectsV2(req);
        final List<S3ObjectSummary> objectSummaries = listing.getObjectSummaries();
        return objectSummaries.stream()
                .filter(objectSummary -> {
                    final List<String> keyParts = List.of(objectSummary.getKey().split("/"));
                    final String prefix = keyParts.stream().limit(3).collect(Collectors.joining("/"));
                    final List<S3ObjectSummary> matchingObjectSummaries = getAllFromPrefix(objectSummaries, prefix);
                    return matchingObjectSummaries.stream()
                            .allMatch(os -> os.getLastModified().before(objectSummary.getLastModified()) || os.getLastModified().equals(objectSummary.getLastModified()));
                })
                .map(S3ObjectSummary::getKey)
                .filter(filename -> filename.contains("."))
                .collect(Collectors.toList());
    }

    private static List<S3ObjectSummary> getAllFromPrefix(final List<S3ObjectSummary> objectSummaries, final String prefix) {
        return objectSummaries.stream()
                .filter(objectSummary -> objectSummary.getKey().startsWith(prefix))
                .collect(Collectors.toList());
    }

    private static void downloadFile(final String fileName) {
        try {
            File localFile = new File(TMP_DIR + fileName);
            client.getObject(new GetObjectRequest(SUBMISSION_ATTACHMENTS_BUCKET_NAME, fileName), localFile);
        } catch (AmazonServiceException e) {
            logger.error("Could not download file: " + fileName + " from bucket: " + SUBMISSION_ATTACHMENTS_BUCKET_NAME,
                    e);
            throw e;
        }
    }

    public static String parseFileName(final String objectKey, int suffix, final String applicationId,
                                       final String submissionId) {
        final String filenameWithoutFolderName = getFileNameFromS3ObjectKey(objectKey, applicationId, submissionId);
        final String[] fileNameParts = filenameWithoutFolderName.split("\\.");
        final String fileExtension = "." + fileNameParts[fileNameParts.length - 1];
        final String filenameWithoutExtension = filenameWithoutFolderName.replace(fileExtension, "");

        // Need to trim very long file names to prevent max path length errors in windows
        final String truncatedFileName = filenameWithoutExtension.length() > LONG_FILE_NAME_LENGTH ?
                filenameWithoutExtension.substring(0, LONG_FILE_NAME_LENGTH).trim() : filenameWithoutExtension;

        return truncatedFileName.concat("_" + suffix + fileExtension);
    }

    public static void deleteTmpDirContents() {
        try {
            FileUtils.cleanDirectory(new File(TMP_DIR));
        } catch (IOException e) {
            logger.error("Could not delete the contents of the tmp directory", e);
        }
    }

    private static ByteArrayOutputStream zipFiles(final List<String> files, final String applicationId,
                                 final String submissionId,final byte[] odtContent) throws IOException {
        try (final ByteArrayOutputStream fout = new ByteArrayOutputStream();
             final ZipOutputStream zout = new ZipOutputStream(fout)) {
            int index = 1;
            for (String filename : files) {
                addFileToZip(filename, zout, index, applicationId, submissionId);
                index++;
            }
            ZipEntry submissionEntry = new ZipEntry("submission.odt");
            zout.putNextEntry(submissionEntry);
            zout.write(odtContent);
            zout.closeEntry();
            return fout;

        } catch (FileNotFoundException e) {
            logger.error("Could not create the locally zipped file: " + LOCAL_ZIP_FILE_NAME, e);
            throw e;
        } catch (IOException e) {
            logger.error("IO exception while creating the empty zipped file", e);
            throw e;
        }
    }

    private static void addFileToZip(final String filename, final ZipOutputStream zout,
                                     final int index, final String applicationId,
                                     final String submissionId) throws IOException {
        try (final FileInputStream fis = new FileInputStream(TMP_DIR + filename)) {
            final ZipEntry ze = new ZipEntry(parseFileName(filename, index, applicationId, submissionId));
            zout.putNextEntry(ze);
            // Copy file contents over to zip entry
            int length;
            byte[] buffer = new byte[1024];
            while ((length = fis.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            zout.closeEntry();
        } catch (FileNotFoundException e) {
            logger.error("Could not create a zip entry with the name: " + filename, e);
            throw e;
        } catch (IOException e) {
            logger.error("IO exception while creating the zip entry with the name: " + filename, e);
            throw e;
        }
    }

    private static String getFileNameFromS3ObjectKey(String objectKey, String applicationId, String submissionId) {
        //an object key is formed by applicationId/submissionId/s3bucketRandomFolderName/filename
        final String applicationIdAndSubmissionId = applicationId + "/" + submissionId + "/";
        final String filenameWithoutApplicationIdAndSubmissionId = objectKey.replace(applicationIdAndSubmissionId, "");
        final String folderNameToRemove = filenameWithoutApplicationIdAndSubmissionId.split("/")[0];
        return filenameWithoutApplicationIdAndSubmissionId.replace(folderNameToRemove + "/", "").replaceAll(SPECIAL_CHARACTER_REGEX, "_");
    }
}
