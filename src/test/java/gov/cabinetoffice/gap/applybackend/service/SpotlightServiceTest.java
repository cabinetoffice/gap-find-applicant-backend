package gov.cabinetoffice.gap.applybackend.service;

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
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
class SpotlightServiceTest {

    @Mock
    private SpotlightSubmissionRepository spotlightSubmissionRepository;

    private final String SPOOKY_DAY_OOOOO = "2023-10-31T12:00:00.00z";
    private final Clock clock = Clock.fixed(Instant.parse(SPOOKY_DAY_OOOOO), ZoneId.of("UTC"));

    private SpotlightService serviceUnderTest;

    @BeforeEach
    void setup() {
        serviceUnderTest = new SpotlightService(spotlightSubmissionRepository, clock);
    }

    @Test
    void createSpotlightCheck_CreatesDatabaseEntry_AndAddsToQueue() {

        final GrantMandatoryQuestions mqs = GrantMandatoryQuestions.builder().build();
        final GrantScheme scheme = GrantScheme.builder()
                .version(2)
                .build();

        final ArgumentCaptor<SpotlightSubmission> spotlightSubmissionCaptor = ArgumentCaptor.forClass(SpotlightSubmission.class);

        serviceUnderTest.createSpotlightCheck(mqs, scheme);

        verify(spotlightSubmissionRepository).save(spotlightSubmissionCaptor.capture());

        final SpotlightSubmission spotlightSubmission = spotlightSubmissionCaptor.getValue();

        assertThat(spotlightSubmission.getMandatoryQuestions()).isEqualTo(mqs);
        assertThat(spotlightSubmission.getGrantScheme()).isEqualTo(scheme);
        assertThat(spotlightSubmission.getVersion()).isEqualTo(2);
        assertThat(spotlightSubmission.getLastUpdated()).isEqualTo(Instant.now(clock));
        assertThat(spotlightSubmission.getStatus()).isEqualTo(SpotlightSubmissionStatus.QUEUED.toString());

        // TODO remove once SQS has been integrated
        fail("Need to add to the queue before this test can pass");
    }
}