package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import gov.cabinetoffice.gap.applybackend.config.properties.S3ConfigProperties;
import gov.cabinetoffice.gap.applybackend.model.GrantAttachment;
import gov.cabinetoffice.gap.applybackend.repository.GrantAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final S3ConfigProperties s3Properties;
    private final GrantAttachmentRepository grantAttachmentRepository;

    public String attachmentFile(String fileObjKeyName, MultipartFile attachment) {
        try {
            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.EU_WEST_2)
                    .build();

            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(attachment.getSize());

            final PutObjectRequest request = new PutObjectRequest(s3Properties.getBucket(), fileObjKeyName, attachment.getInputStream(), metadata);
            s3Client.putObject(request);
            URL s3Url = s3Client.getUrl(s3Properties.getBucket(), fileObjKeyName);
            return s3Url.toExternalForm();
        } catch (SdkClientException | IOException e) {
            log.error("An error occurred saving the file: ", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteAttachment(final GrantAttachment attachment, final Integer applicationId, final UUID submissionId, final String questionId) {

        final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build();

        try {
            final AmazonS3URI s3Uri = new AmazonS3URI(attachment.getLocation());
            final String key = applicationId + "/" + submissionId + "/" + questionId + "/" + attachment.getFilename();
            s3Client.deleteObject(new DeleteObjectRequest(s3Uri.getBucket(), key));
            s3Client.deleteObject(new DeleteObjectRequest(s3Properties.getAttachmentsBucket(), key));
            grantAttachmentRepository.delete(attachment);
        } catch (SdkClientException e) {
            log.error("An error occurred deleting the file: ", e);
        }
    }
}
