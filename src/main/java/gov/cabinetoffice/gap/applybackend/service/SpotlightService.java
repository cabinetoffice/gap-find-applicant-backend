package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.SpotlightSubmission;
import gov.cabinetoffice.gap.applybackend.repository.SpotlightSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpotlightService {

    private final SpotlightSubmissionRepository spotlightSubmissionRepository;
    private final Clock clock;

    public void createSpotlightCheck(GrantMandatoryQuestions mandatoryQuestions, GrantScheme scheme) {

        // save a spotlight submission object to the database
        final SpotlightSubmission spotlightSubmission = SpotlightSubmission.builder()
                .mandatoryQuestions(mandatoryQuestions)
                .grantScheme(scheme)
                .version(scheme.getVersion()) //TODO is this how we want to set the version?
                .lastUpdated(Instant.now(clock))
                .build();

        spotlightSubmissionRepository.save(spotlightSubmission);

        // send that object to SQS for processing

    }
}
