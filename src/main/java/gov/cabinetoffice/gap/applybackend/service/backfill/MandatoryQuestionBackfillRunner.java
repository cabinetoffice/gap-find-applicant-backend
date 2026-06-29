package gov.cabinetoffice.gap.applybackend.service.backfill;

import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.model.DiligenceCheck;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.repository.DiligenceCheckRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * One-off data backfill that restores the one-to-one relationship between a submission and its
 * mandatory questions.
 *
 * <p>Some historical multi-submission applications were submitted without their own mandatory
 * question record (a previous bug shared a single record across submissions). This runner finds
 * those submissions and creates the missing record for each one, using the data that was captured
 * at the time of submission:
 * <ul>
 *     <li>funding amount and organisation details come from the {@code diligence_check} table</li>
 *     <li>funding location comes from the {@code grant_beneficiary} table</li>
 *     <li>organisation type comes from the submission's stored answers (the definition JSON)</li>
 * </ul>
 *
 * <p>A mandatory question record is always created for every orphaned submission, even when none of
 * that supporting data is available — in that case the record is created with empty values so the
 * one-to-one relationship still exists. Any record created with missing data is logged as a warning.
 *
 * <p>This runner only executes when the application is started with the {@code backfill} Spring
 * profile active (for example {@code --spring.profiles.active=backfill}). It does nothing in normal
 * operation.
 */
@Component
@Profile("backfill")
@RequiredArgsConstructor
@Slf4j
public class MandatoryQuestionBackfillRunner implements ApplicationRunner {

    private static final String APPLICANT_TYPE_QUESTION_ID = "APPLICANT_TYPE";

    private final SubmissionRepository submissionRepository;
    private final DiligenceCheckRepository diligenceCheckRepository;
    private final GrantBeneficiaryRepository grantBeneficiaryRepository;
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;

    @Override
    public void run(final ApplicationArguments args) {
        final List<Submission> orphanedSubmissions = submissionRepository
                .findSubmittedMultiSubmissionWithoutMandatoryQuestions();

        log.info("Mandatory question backfill starting. Found {} submitted submission(s) with no mandatory question record.",
                orphanedSubmissions.size());

        int created = 0;
        int partial = 0;

        for (final Submission submission : orphanedSubmissions) {
            final GrantMandatoryQuestions mandatoryQuestions = buildMandatoryQuestionsForSubmission(submission);
            grantMandatoryQuestionRepository.save(mandatoryQuestions);
            created++;

            if (isPartial(mandatoryQuestions)) {
                partial++;
                log.warn("Created a partial mandatory question record for submission {} (gapId {}). "
                                + "Some data could not be found and was left empty.",
                        submission.getId(), submission.getGapId());
            }
        }

        log.info("Mandatory question backfill complete. Processed {}, created {}, of which {} were partial.",
                orphanedSubmissions.size(), created, partial);
    }

    private GrantMandatoryQuestions buildMandatoryQuestionsForSubmission(final Submission submission) {
        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .grantScheme(submission.getScheme())
                .submission(submission)
                .createdBy(submission.getApplicant())
                .gapId(submission.getGapId())
                .status(GrantMandatoryQuestionStatus.COMPLETED)
                .build();

        applyDiligenceCheckData(submission, mandatoryQuestions);
        applyBeneficiaryData(submission, mandatoryQuestions);
        applyOrgType(submission, mandatoryQuestions);

        return mandatoryQuestions;
    }

    /**
     * Copies the organisation details and funding amount that were recorded against the submission
     * in the diligence check table. Leaves the fields empty if there is no diligence check record.
     */
    private void applyDiligenceCheckData(final Submission submission, final GrantMandatoryQuestions mandatoryQuestions) {
        final DiligenceCheck diligenceCheck = diligenceCheckRepository
                .findBySubmissionId(submission.getId())
                .orElse(null);

        if (diligenceCheck == null) {
            return;
        }

        mandatoryQuestions.setName(diligenceCheck.getOrganisationName());
        mandatoryQuestions.setAddressLine1(diligenceCheck.getAddressStreet());
        mandatoryQuestions.setCity(diligenceCheck.getAddressTown());
        mandatoryQuestions.setCounty(diligenceCheck.getAddressCounty());
        mandatoryQuestions.setPostcode(diligenceCheck.getAddressPostcode());
        mandatoryQuestions.setCompaniesHouseNumber(diligenceCheck.getCompaniesHouseNumber());
        mandatoryQuestions.setCharityCommissionNumber(diligenceCheck.getCharityNumber());
        mandatoryQuestions.setFundingAmount(parseFundingAmount(diligenceCheck.getApplicationAmount(), submission));
    }

    /**
     * Copies the funding location that was recorded against the submission in the beneficiary table.
     * Leaves the funding location empty if there is no beneficiary record.
     */
    private void applyBeneficiaryData(final Submission submission, final GrantMandatoryQuestions mandatoryQuestions) {
        final GrantBeneficiary beneficiary = grantBeneficiaryRepository
                .findBySubmissionId(submission.getId())
                .orElse(null);

        if (beneficiary == null) {
            return;
        }

        mandatoryQuestions.setFundingLocation(mapBeneficiaryLocations(beneficiary));
    }

    /**
     * Reads the organisation type from the answers the applicant submitted (the definition JSON),
     * which is the most accurate record of what they chose at submission time. Leaves it empty if
     * the answer cannot be found.
     */
    private void applyOrgType(final Submission submission, final GrantMandatoryQuestions mandatoryQuestions) {
        if (submission.getDefinition() == null || submission.getDefinition().getSections() == null) {
            return;
        }

        submission.getDefinition().getSections().stream()
                .filter(section -> section.getQuestions() != null)
                .flatMap(section -> section.getQuestions().stream())
                .filter(question -> APPLICANT_TYPE_QUESTION_ID.equals(question.getQuestionId()))
                .map(question -> question.getResponse())
                .filter(response -> response != null && !response.isBlank())
                .findFirst()
                .map(GrantMandatoryQuestionOrgType::valueOfName)
                .ifPresent(mandatoryQuestions::setOrgType);
    }

    private BigDecimal parseFundingAmount(final String applicationAmount, final Submission submission) {
        if (applicationAmount == null || applicationAmount.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(applicationAmount.trim());
        } catch (final NumberFormatException e) {
            log.warn("Could not parse funding amount '{}' for submission {} (gapId {}). Leaving it empty.",
                    applicationAmount, submission.getId(), submission.getGapId());
            return null;
        }
    }

    private GrantMandatoryQuestionFundingLocation[] mapBeneficiaryLocations(final GrantBeneficiary beneficiary) {
        final List<GrantMandatoryQuestionFundingLocation> locations = new ArrayList<>();

        addIfTrue(locations, beneficiary.getLocationNeEng(), GrantMandatoryQuestionFundingLocation.NORTH_EAST_ENGLAND);
        addIfTrue(locations, beneficiary.getLocationNwEng(), GrantMandatoryQuestionFundingLocation.NORTH_WEST_ENGLAND);
        addIfTrue(locations, beneficiary.getLocationSeEng(), GrantMandatoryQuestionFundingLocation.SOUTH_EAST_ENGLAND);
        addIfTrue(locations, beneficiary.getLocationSwEng(), GrantMandatoryQuestionFundingLocation.SOUTH_WEST_ENGLAND);
        addIfTrue(locations, beneficiary.getLocationMidEng(), GrantMandatoryQuestionFundingLocation.MIDLANDS);
        addIfTrue(locations, beneficiary.getLocationSco(), GrantMandatoryQuestionFundingLocation.SCOTLAND);
        addIfTrue(locations, beneficiary.getLocationWal(), GrantMandatoryQuestionFundingLocation.WALES);
        addIfTrue(locations, beneficiary.getLocationNir(), GrantMandatoryQuestionFundingLocation.NORTHERN_IRELAND);
        addIfTrue(locations, beneficiary.getLocationLon(), GrantMandatoryQuestionFundingLocation.LONDON);
        addIfTrue(locations, beneficiary.getLocationOutUk(), GrantMandatoryQuestionFundingLocation.OUTSIDE_UK);

        if (locations.isEmpty()) {
            return null;
        }

        return locations.toArray(new GrantMandatoryQuestionFundingLocation[0]);
    }

    private void addIfTrue(final List<GrantMandatoryQuestionFundingLocation> locations,
            final Boolean flag, final GrantMandatoryQuestionFundingLocation location) {
        if (Boolean.TRUE.equals(flag)) {
            locations.add(location);
        }
    }

    /**
     * A record is "partial" if any of the key data we were trying to recover is missing. These are
     * logged so they can be reviewed manually after the backfill has run.
     */
    private boolean isPartial(final GrantMandatoryQuestions mandatoryQuestions) {
        return mandatoryQuestions.getName() == null
                || mandatoryQuestions.getFundingAmount() == null
                || mandatoryQuestions.getFundingLocation() == null
                || mandatoryQuestions.getOrgType() == null;
    }
}
