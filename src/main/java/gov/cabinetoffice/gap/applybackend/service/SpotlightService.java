package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import gov.cabinetoffice.gap.applybackend.config.SpotlightQueueConfigProperties;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.SpotlightSubmission;
import gov.cabinetoffice.gap.applybackend.repository.SpotlightSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpotlightService {

    public static final int SPOTLIGHT_SUBSCRIPTION_VERSION = 1;
    private final SpotlightSubmissionRepository spotlightSubmissionRepository;
    private final AmazonSQS amazonSqs;
    private final SpotlightQueueConfigProperties spotlightQueueProperties;
    private final Clock clock;

    public void createSpotlightCheck(GrantMandatoryQuestions mandatoryQuestions, GrantScheme scheme) {

        // save a spotlight submission object to the database
        final SpotlightSubmission spotlightSubmission = SpotlightSubmission.builder()
                .mandatoryQuestions(mandatoryQuestions)
                .grantScheme(scheme)
                .version(SPOTLIGHT_SUBSCRIPTION_VERSION)
                .lastUpdated(Instant.now(clock))
                .build();

        final SpotlightSubmission savedSpotlightSubmission = spotlightSubmissionRepository.save(spotlightSubmission);

        // send that object to SQS for processing
        final UUID messageId = UUID.randomUUID();
        final MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(Instant.now().toString());

        final SendMessageRequest messageRequest = new SendMessageRequest()
                .withQueueUrl(spotlightQueueProperties.getQueueUrl())
                .withMessageGroupId(messageId.toString())
                .withMessageBody(savedSpotlightSubmission.getId().toString())
                .withMessageDeduplicationId(messageId.toString())
                .withMessageAttributes(Map.of("SentTimeStamp", messageAttributeValue));

        amazonSqs.sendMessage(messageRequest);
    }
}
