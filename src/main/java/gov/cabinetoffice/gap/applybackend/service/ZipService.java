package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.config.S3Config;
import gov.cabinetoffice.gap.applybackend.utils.ZipHelper;
import lombok.RequiredArgsConstructor;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Service
public class ZipService {

    @Value("${aws.bucket}")
    private String s3Bucket;

    private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

    //regex for any special character that are not allowed in window os : <, >, ", /, \, |, ?, or *
    private static final String SPECIAL_CHARACTER_REGEX = "[<>\"\\/|?*\\\\]";

    public static final Integer LONG_FILE_NAME_LENGTH = 50;
    private final AmazonS3 client = S3Config.initializeS3();


    public ByteArrayResource byteArrayOutputStreamToResource(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayResource(zipBytes);
    }

    public ByteArrayOutputStream createSubmissionZip(final Submission submission, final OdfTextDocument odtDoc)
            throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        odtDoc.save(outputStream);
        byte[] odt = outputStream.toByteArray();

        String submissionId = String.valueOf(submission.getId());
        String applicationId = String.valueOf(submission.getApplication().getId());
        String odtFilename = ZipHelper.generateFilename(submission.getLegalName());

        final List<String> submissionAttachmentFileNames =
                getSubmissionAttachmentFileNames(applicationId, submissionId);

        List<S3Object> s3ObjectList = new ArrayList<>();
        for (String fileName : submissionAttachmentFileNames) {
            downloadFile(fileName, s3ObjectList);
        }

        ByteArrayOutputStream zip = zipFiles(s3ObjectList, applicationId, submissionId, odt, odtFilename);
        logger.info("Zip file created");
        return zip;
    }

    public List<String> getSubmissionAttachmentFileNames(final String applicationId,
                                                          final String submissionId) {
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3Bucket)
                .withPrefix(applicationId + "/" + submissionId);
        final ListObjectsV2Result listing = client.listObjectsV2(req);
        final List<S3ObjectSummary> objectSummaries = listing.getObjectSummaries();
        return objectSummaries.stream()
                .filter(objectSummary -> {
                    final List<String> keyParts = List.of(objectSummary.getKey().split("/"));
                    final String prefix = keyParts.stream().limit(3).collect(Collectors.joining("/"));
                    final List<S3ObjectSummary> matchingObjectSummaries = getAllFromPrefix(objectSummaries, prefix);
                    return matchingObjectSummaries.stream()
                            .allMatch(os -> os.getLastModified().before(objectSummary.getLastModified()) ||
                                    os.getLastModified().equals(objectSummary.getLastModified()));
                })
                .map(S3ObjectSummary::getKey)
                .filter(filename -> filename.contains("."))
                .collect(Collectors.toList());
    }

    private List<S3ObjectSummary> getAllFromPrefix(final List<S3ObjectSummary> objectSummaries, final String prefix) {
        return objectSummaries.stream()
                .filter(objectSummary -> objectSummary.getKey().startsWith(prefix))
                .collect(Collectors.toList());
    }

    private void downloadFile(final String fileName, List<S3Object> list) {
        try {
            list.add(client.getObject(new GetObjectRequest(s3Bucket, fileName)));
        } catch (AmazonServiceException e) {
            logger.error("Could not download file: " + fileName + " from bucket: " + s3Bucket,
                    e);
            throw e;
        }
    }

    public String parseFileName(final String objectKey, int suffix, final String applicationId,
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
    private ByteArrayOutputStream zipFiles(final List<S3Object> files, final String applicationId,
                                 final String submissionId,final byte[] odtContent, final String odtFilename) throws IOException {
        try (final ByteArrayOutputStream fout = new ByteArrayOutputStream();
             final ZipOutputStream zout = new ZipOutputStream(fout)) {
            int index = 1;
            for (S3Object file : files) {
                addFileToZip(file, zout, index, applicationId, submissionId);
                index++;
            }
            ZipEntry submissionEntry = new ZipEntry(odtFilename + ".odt");
            zout.putNextEntry(submissionEntry);
            zout.write(odtContent);
            zout.closeEntry();
            return fout;

        } catch (IOException e) {
            logger.error("IO exception while creating the empty zipped file", e);
            throw e;
        }
    }

    private void addFileToZip(final S3Object file, final ZipOutputStream zout,
                                     final int index, final String applicationId,
                                     final String submissionId) throws IOException {
        try (final InputStream s3ObjectStream = file.getObjectContent()) {
            final ZipEntry ze = new ZipEntry(parseFileName(file.getKey(), index, applicationId, submissionId));
            zout.putNextEntry(ze);
            // Copy file contents over to zip entry
            int length;
            byte[] buffer = new byte[1024];
            while ((length = s3ObjectStream.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            zout.closeEntry();
        } catch (FileNotFoundException e) {
            logger.error("Could not create a zip entry with the name: " + file.getKey(), e);
            throw e;
        } catch (IOException e) {
            logger.error("IO exception while creating the zip entry with the name: " + file.getKey(), e);
            throw e;
        }
    }

    private String getFileNameFromS3ObjectKey(String objectKey, String applicationId, String submissionId) {
        //an object key is formed by applicationId/submissionId/s3bucketRandomFolderName/filename
        final String applicationIdAndSubmissionId = applicationId + "/" + submissionId + "/";
        final String filenameWithoutApplicationIdAndSubmissionId = objectKey.replace(applicationIdAndSubmissionId,
                "");
        final String folderNameToRemove = filenameWithoutApplicationIdAndSubmissionId.split("/")[0];
        return filenameWithoutApplicationIdAndSubmissionId.replace(folderNameToRemove + "/", "")
                .replaceAll(SPECIAL_CHARACTER_REGEX, "_");
    }
}
