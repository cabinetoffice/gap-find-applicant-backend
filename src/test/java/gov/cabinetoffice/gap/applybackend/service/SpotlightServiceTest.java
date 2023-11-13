package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import gov.cabinetoffice.gap.applybackend.config.SpotlightQueueConfigProperties;
import gov.cabinetoffice.gap.applybackend.enums.SpotlightSubmissionStatus;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.SpotlightSubmission;
import gov.cabinetoffice.gap.applybackend.repository.SpotlightSubmissionRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SpotlightServiceTest {

    @Mock
    private SpotlightSubmissionRepository spotlightSubmissionRepository;

    @Mock
    private AmazonSQS amazonSqs;

    private SpotlightQueueConfigProperties spotlightQueueProperties;
    private final String SPOOKY_DAY_OOOOO = "2023-10-31T12:00:00.00z";
    private final Clock clock = Clock.fixed(Instant.parse(SPOOKY_DAY_OOOOO), ZoneId.of("UTC"));

    private SpotlightService serviceUnderTest;

    @BeforeEach
    void setup() {
        spotlightQueueProperties = SpotlightQueueConfigProperties.builder()
                .spotlightQueue("a-queue-url")
                .build();

        serviceUnderTest = new SpotlightService(spotlightSubmissionRepository, amazonSqs, spotlightQueueProperties, clock);
    }

    @Test
    void createSpotlightCheck_CreatesDatabaseEntry_AndAddsToQueue() {

        final GrantMandatoryQuestions mqs = GrantMandatoryQuestions.builder().build();
        final GrantScheme scheme = GrantScheme.builder()
                .version(2)
                .build();

        final ArgumentCaptor<SpotlightSubmission> spotlightSubmissionCaptor = ArgumentCaptor.forClass(SpotlightSubmission.class);

        final SpotlightSubmission submissionAfterSave = SpotlightSubmission.builder()
                .id(UUID.randomUUID())
                .build();

        when(spotlightSubmissionRepository.save(Mockito.any()))
                .thenReturn(submissionAfterSave);

        final ArgumentCaptor<SendMessageRequest> sqsRequestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);

        serviceUnderTest.createSpotlightCheck(mqs, scheme);

        // Test that the spotlight submission has been saved to the relational database
        verify(spotlightSubmissionRepository).save(spotlightSubmissionCaptor.capture());

        final SpotlightSubmission spotlightSubmission = spotlightSubmissionCaptor.getValue();

        assertThat(spotlightSubmission.getMandatoryQuestions()).isEqualTo(mqs);
        assertThat(spotlightSubmission.getGrantScheme()).isEqualTo(scheme);
        assertThat(spotlightSubmission.getVersion()).isEqualTo(2);
        assertThat(spotlightSubmission.getLastUpdated()).isEqualTo(Instant.now(clock));
        assertThat(spotlightSubmission.getStatus()).isEqualTo(SpotlightSubmissionStatus.QUEUED.toString());


        // Test that the spotlight submission has been sent to SQS
        verify(amazonSqs).sendMessage(sqsRequestCaptor.capture());

        final SendMessageRequest sqsRequest = sqsRequestCaptor.getValue();

        assertThat(sqsRequest.getMessageBody()).isNotNull();
        assertThat(sqsRequest.getQueueUrl()).isEqualTo(spotlightQueueProperties.getSpotlightQueue());
        assertThat(sqsRequest.getMessageAttributes().get(SpotlightService.SPOTLIGHT_SUBMISSION_ID).getStringValue()).isEqualTo(submissionAfterSave.getId().toString());
    }
}