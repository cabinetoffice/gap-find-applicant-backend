package gov.cabinetoffice.gap.eventservice.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cabinetoffice.gap.eventservice.dto.EventLog;
import gov.cabinetoffice.gap.eventservice.enums.EventType;
import gov.cabinetoffice.gap.eventservice.enums.ObjectType;
import gov.cabinetoffice.gap.applybackend.testingextensions.ErrorLogCapture;
import gov.cabinetoffice.gap.applybackend.testingextensions.ErrorLogCaptureExtension;
import gov.cabinetoffice.gap.applybackend.testingextensions.InfoLogCapture;
import gov.cabinetoffice.gap.applybackend.testingextensions.InfoLogCaptureExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ExtendWith({ InfoLogCaptureExtension.class, ErrorLogCaptureExtension.class })
class EventLogServiceTest {

    @Mock
    private AmazonSQS amazonSQS;

    @Mock
    private ObjectMapper objectMapper;

    private EventLogService eventLogService;

    private final Clock clock = Clock.fixed(Instant.parse("2023-01-01T12:00:00.00Z"), ZoneId.systemDefault());

    @Captor
    private ArgumentCaptor<EventLog> eventLogArgumentCaptor;

    @BeforeEach
    void setUp() {
        eventLogService = new EventLogService("eventLogQueue", true, amazonSQS, objectMapper, clock);
    }

    @Nested
    class logSubmissionCreatedEvent {

        @Test
        public void success(InfoLogCapture logCapture) throws JsonProcessingException {

            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            when(objectMapper.writeValueAsString(eventLogArgumentCaptor.capture())).thenReturn("");
            when(amazonSQS.sendMessage(anyString(), any())).thenReturn(null);

            eventLogService.logSubmissionCreatedEvent(sessionId, userSub, objectId);

            EventLog actualEventLog = eventLogArgumentCaptor.getValue();

            assertThat(actualEventLog.getEventType()).isEqualTo(EventType.SUBMISSION_CREATED);
            assertThat(actualEventLog.getSessionId()).isEqualTo(sessionId);
            assertThat(actualEventLog.getUserSub()).isEqualTo(userSub);
            assertThat(actualEventLog.getObjectId()).isEqualTo(objectId);
            assertThat(actualEventLog.getObjectType()).isEqualTo(ObjectType.SUBMISSION);
            assertThat(actualEventLog.getTimestamp()).isEqualTo(clock.instant());

            assertThat(logCapture.getLoggingEventAt(1).getFormattedMessage())
                    .isEqualTo(EventType.SUBMISSION_CREATED + " Message sent successfully");
        }

        @Test
        public void cantSendToSQS(ErrorLogCapture logCapture) throws JsonProcessingException {
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            String expectedMessageBody = "MessageBody";
            when(objectMapper.writeValueAsString(any(EventLog.class))).thenReturn(expectedMessageBody);
            when(amazonSQS.sendMessage(anyString(), anyString())).thenThrow(AmazonSQSException.class);

            eventLogService.logSubmissionCreatedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .startsWith("Message failed to send for event log");

        }

        @Test
        public void queueDisabled(InfoLogCapture logCapture) {
            eventLogService = new EventLogService("eventLogQueue", false, amazonSQS, objectMapper, clock);
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            eventLogService.logSubmissionCreatedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .isEqualTo("Event Service Queue is disabled. Returning without sending.");
            verifyNoInteractions(amazonSQS, objectMapper);
        }

    }

    @Nested
    class logSubmissionUpdatedEvent {

        @Test
        public void success(InfoLogCapture logCapture) throws JsonProcessingException {

            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            when(objectMapper.writeValueAsString(eventLogArgumentCaptor.capture())).thenReturn("");
            when(amazonSQS.sendMessage(anyString(), any())).thenReturn(null);

            eventLogService.logSubmissionUpdatedEvent(sessionId, userSub, objectId);

            EventLog actualEventLog = eventLogArgumentCaptor.getValue();

            assertThat(actualEventLog.getEventType()).isEqualTo(EventType.SUBMISSION_UPDATED);
            assertThat(actualEventLog.getSessionId()).isEqualTo(sessionId);
            assertThat(actualEventLog.getUserSub()).isEqualTo(userSub);
            assertThat(actualEventLog.getObjectId()).isEqualTo(objectId);
            assertThat(actualEventLog.getObjectType()).isEqualTo(ObjectType.SUBMISSION);
            assertThat(actualEventLog.getTimestamp()).isEqualTo(clock.instant());

            assertThat(logCapture.getLoggingEventAt(1).getFormattedMessage())
                    .isEqualTo(EventType.SUBMISSION_UPDATED + " Message sent successfully");
        }

        @Test
        public void cantSendToSQS(ErrorLogCapture logCapture) throws JsonProcessingException {
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            String expectedMessageBody = "MessageBody";
            when(objectMapper.writeValueAsString(any(EventLog.class))).thenReturn(expectedMessageBody);
            when(amazonSQS.sendMessage(anyString(), anyString())).thenThrow(AmazonSQSException.class);

            eventLogService.logSubmissionUpdatedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .startsWith("Message failed to send for event log");

        }

        @Test
        public void queueDisabled(InfoLogCapture logCapture) {
            eventLogService = new EventLogService("eventLogQueue", false, amazonSQS, objectMapper, clock);
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            eventLogService.logSubmissionUpdatedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .isEqualTo("Event Service Queue is disabled. Returning without sending.");
            verifyNoInteractions(amazonSQS, objectMapper);
        }

    }

    @Nested
    class logSubmissionPublishedEvent {

        @Test
        public void success(InfoLogCapture logCapture) throws JsonProcessingException {

            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            when(objectMapper.writeValueAsString(eventLogArgumentCaptor.capture())).thenReturn("");
            when(amazonSQS.sendMessage(anyString(), any())).thenReturn(null);

            eventLogService.logSubmissionPublishedEvent(sessionId, userSub, objectId);

            EventLog actualEventLog = eventLogArgumentCaptor.getValue();

            assertThat(actualEventLog.getEventType()).isEqualTo(EventType.SUBMISSION_PUBLISHED);

            assertThat(actualEventLog.getSessionId()).isEqualTo(sessionId);
            assertThat(actualEventLog.getUserSub()).isEqualTo(userSub);
            assertThat(actualEventLog.getObjectId()).isEqualTo(objectId);
            assertThat(actualEventLog.getObjectType()).isEqualTo(ObjectType.SUBMISSION);
            assertThat(actualEventLog.getTimestamp()).isEqualTo(clock.instant());

            assertThat(logCapture.getLoggingEventAt(1).getFormattedMessage())
                    .isEqualTo(EventType.SUBMISSION_PUBLISHED + " Message sent successfully");
        }

        @Test
        public void cantSendToSQS(ErrorLogCapture logCapture) throws JsonProcessingException {
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            String expectedMessageBody = "MessageBody";
            when(objectMapper.writeValueAsString(any(EventLog.class))).thenReturn(expectedMessageBody);
            when(amazonSQS.sendMessage(anyString(), anyString())).thenThrow(AmazonSQSException.class);

            eventLogService.logSubmissionPublishedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .startsWith("Message failed to send for event log");

        }

        @Test
        public void queueDisabled(InfoLogCapture logCapture) {
            eventLogService = new EventLogService("eventLogQueue", false, amazonSQS, objectMapper, clock);
            String sessionId = "SessionId";
            String userSub = "UserSub";
            String objectId = "ObjectId";

            eventLogService.logSubmissionPublishedEvent(sessionId, userSub, objectId);

            assertThat(logCapture.getLoggingEventAt(0).getFormattedMessage())
                    .isEqualTo("Event Service Queue is disabled. Returning without sending.");
            verifyNoInteractions(amazonSQS, objectMapper);
        }
    }
}