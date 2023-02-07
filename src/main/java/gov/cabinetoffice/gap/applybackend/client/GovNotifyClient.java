package gov.cabinetoffice.gap.applybackend.client;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.config.properties.GovNotifyProperties;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GovNotifyClient {

    private final NotificationClient notificationClient;
    private final GovNotifyProperties notifyProperties;
    private final EnvironmentProperties environmentProperties;

    public void sendConfirmationEmail(final String emailAddress, final Submission submission) {
        final Map<String, Object> personalisation = Map.of(
                "name of grant", submission.getApplicationName(),
                "link to find a grant sign in", String.format("%s/applications", environmentProperties.getFrontEndUri())
        );
        final String reference = String.format("submission-%s", submission.getId());

        try {
            sendEmail(notifyProperties.getSubmissionConfirmationTemplate(), emailAddress, personalisation, reference);
        } catch (NotificationClientException e) {
            log.error("Failed to send submission confirmation email for submission: {}", submission.getId(), e);
        }
    }

    private void sendEmail(final String template, final String emailAddress, final Map<String, Object> personalisation, final String reference) throws NotificationClientException {
        notificationClient.sendEmail(template, emailAddress, personalisation, reference);
    }
}
