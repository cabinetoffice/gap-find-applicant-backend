package gov.cabinetoffice.gap.eventservice.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cabinetoffice.gap.eventservice.dto.EventLog;
import gov.cabinetoffice.gap.eventservice.enums.EventType;
import gov.cabinetoffice.gap.eventservice.enums.ObjectType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Service
public class EventLogService {

    private final String eventLogQueue;

    private final boolean eventServiceQueueEnabled;

    private final AmazonSQS amazonSQS;

    private final ObjectMapper objectMapper;

    private final Clock clock;

    public EventLogService(@Value("${cloud.aws.sqs.event-service-queue}") String eventLogQueue,
                           @Value("${cloud.aws.sqs.event-service-queue-enabled}") boolean eventServiceQueueEnabled,
                           AmazonSQS amazonSQS, ObjectMapper objectMapper, Clock clock) {
        this.eventLogQueue = eventLogQueue;
        this.eventServiceQueueEnabled = eventServiceQueueEnabled;
        this.amazonSQS = amazonSQS;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public void logSubmissionCreatedEvent(String sessionId, String userSub, String objectId) {

        EventLog eventLog = EventLog.builder().objectType(ObjectType.SUBMISSION).eventType(EventType.SUBMISSION_CREATED)
                .sessionId(sessionId).userSub(userSub).objectId(objectId)
                .timestamp(Instant.now(clock)).build();

        logEvent(eventLog);

    }

    public void logSubmissionUpdatedEvent(String sessionId, String userSub, String objectId) {

        EventLog eventLog = EventLog.builder().objectType(ObjectType.SUBMISSION).eventType(EventType.SUBMISSION_UPDATED)
                .sessionId(sessionId).userSub(userSub).objectId(objectId)
                .timestamp(Instant.now(clock)).build();

        logEvent(eventLog);

    }

    public void logSubmissionPublishedEvent(String sessionId, String userSub, String objectId) {
        EventLog eventLog = EventLog.builder().objectType(ObjectType.SUBMISSION).eventType(EventType.SUBMISSION_PUBLISHED)
                .sessionId(sessionId).userSub(userSub).objectId(objectId)
                .timestamp(Instant.now(clock)).build();

        logEvent(eventLog);

    }

    private void logEvent(EventLog eventLog) {

        if (!eventServiceQueueEnabled) {
            log.info("Event Service Queue is disabled. Returning without sending.");
            return;
        }

        try {
            log.info("Sending event to {} : {}", eventLogQueue, eventLog);
            amazonSQS.sendMessage(eventLogQueue, objectMapper.writeValueAsString(eventLog));
            log.info("{} Message sent successfully", eventLog.getEventType());
        } catch (Exception e) {
            log.error("Message failed to send for event log " + eventLog, e);
        }

    }

}
