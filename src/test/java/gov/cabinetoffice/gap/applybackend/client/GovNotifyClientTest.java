package gov.cabinetoffice.gap.applybackend.client;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.config.properties.GovNotifyProperties;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GovNotifyClientTest {

    @Mock
    private NotificationClient notifyClient;
    private GovNotifyProperties notifyProperties;
    private EnvironmentProperties environmentProperties;
    private GovNotifyClient clientUnderTest;

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setup() {
        notifyProperties = GovNotifyProperties.builder()
                .apiKey("an-api-key")
                .submissionConfirmationTemplate("a-template.id")
                .build();

        environmentProperties = EnvironmentProperties.builder()
                .frontEndUri("http://localhost:3000/apply/applicant")
                .build();

        clientUnderTest = new GovNotifyClient(notifyClient, notifyProperties, environmentProperties);

        // might be a bit overkill but we move
        logWatcher = new ListAppender<>();
        logWatcher.start();
        Logger logger = (Logger) LoggerFactory.getLogger(GovNotifyClient.class.getName());
        logger.addAppender(logWatcher);
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(GovNotifyClient.class)).detachAndStopAllAppenders();
    }

    @Test
    void sendConfirmationEmail_sendsEmailWithExpectedProperties() throws NotificationClientException {
        final String email = "test@email.com";
        final Submission submission = Submission.builder()
                .id(UUID.randomUUID())
                .applicationName("Grant to fund treatment for people who think Ronaldo is not a dusty baller.")
                .build();

        final String expectedReference = String.format("submission-%s", submission.getId());
        final Map<String, Object> expectedPersonalisation = Map.of(
                "name of grant", submission.getApplicationName(),
                "link to find a grant sign in", String.format("%s/applications", environmentProperties.getFrontEndUri())
        );

        clientUnderTest.sendConfirmationEmail(email, submission);

        verify(notifyClient).sendEmail(notifyProperties.getSubmissionConfirmationTemplate(), email, expectedPersonalisation, expectedReference);
    }

    @Test
    void sendConfirmationEmail_HandlesNotificationClientException() throws NotificationClientException {
        final String email = "test@email.com";
        final Submission submission = Submission.builder()
                .id(UUID.randomUUID())
                .applicationName("Grant to fund treatment for people who think Ronaldo is not a dusty baller.")
                .build();

        when(notifyClient.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString()))
                .thenThrow(new NotificationClientException("failed to send email"));

        clientUnderTest.sendConfirmationEmail(email, submission);

        assertThat(logWatcher.list.get(0).getFormattedMessage()).contains("");
    }
}