package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.constants.MandatoryQuestionConstants;
import gov.cabinetoffice.gap.applybackend.enums.*;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.GrantApplicationNotPublishedException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantApplicantOrganisationProfileMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import gov.cabinetoffice.gap.applybackend.utils.GapIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantMandatoryQuestionService {
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    private final SubmissionRepository submissionRepository;
    private final GrantApplicantOrganisationProfileMapper organisationProfileMapper;
    private final EnvironmentProperties envProperties;

    public GrantMandatoryQuestions getGrantMandatoryQuestionById(UUID id, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", id))));

        if (grantMandatoryQuestion.isPresent() && !grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with ID %s was not created by %s", id, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    /*
        TODO I think we should decouple access control and CRUD functionality.
        These methods should not require an applicant ID to be passed in to confirm ownership.  
     */
    public GrantMandatoryQuestions getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(UUID submissionId, String applicantSub) {
        Optional<GrantMandatoryQuestions> grantMandatoryQuestion = grantMandatoryQuestionRepository.findBySubmissionId(submissionId);

        if (grantMandatoryQuestion.isEmpty()) {
            // Fallback for multi-app schemes: the MQ record may still be linked to a previous
            // submission. Look up the submission to get the scheme, then find the MQ that way.
            final Integer schemeId = submissionRepository
                    .findByIdAndApplicantUserId(submissionId, applicantSub)
                    .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with submission id %s was found", submissionId)))
                    .getScheme()
                    .getId();

            grantMandatoryQuestion = grantMandatoryQuestionRepository.findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(schemeId, applicantSub);

            if (grantMandatoryQuestion.isEmpty()) {
                throw new NotFoundException(String.format("No Mandatory Question with submission id %s was found", submissionId));
            }
        }

        if (!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with id %s and submission ID %s was not created by %s", grantMandatoryQuestion.get().getId(), submissionId, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions getMandatoryQuestionBySchemeId(Integer schemeId, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository
                .findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(schemeId, applicantSub)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with scheme id  %s was found", schemeId))));

        if (!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with id %s and scheme ID %s was not created by %s",
                    grantMandatoryQuestion.get().getId(), schemeId, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions createMandatoryQuestion(GrantScheme scheme, GrantApplicant applicant, boolean isForInternalApplication) {
        if (mandatoryQuestionExistsBySchemeIdAndApplicantId(scheme.getId(), applicant.getId())) {
            log.debug("Mandatory question for scheme {}, and applicant {} already exist", scheme.getId(), applicant.getId());
            return grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).get(0);
        }

        if (isForInternalApplication &&  scheme.getGrantApplication() != null &&
                scheme.getGrantApplication().getApplicationStatus() == GrantApplicationStatus.REMOVED) {
            throw new GrantApplicationNotPublishedException(String.format("Mandatory question for scheme %d could not be created as the application is not published", scheme.getId()));
        }

        final GrantApplicantOrganisationProfile organisationProfile = applicant.getOrganisationProfile();

        final GrantMandatoryQuestions grantMandatoryQuestions = organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(organisationProfile);

        // Fix to exclude any existing Charity Commission Number or Companies House Number which have invalid lengths,
        //This will force the applicant to go through the MQ journey and update their details with a valid length number
        if (grantMandatoryQuestions.getCharityCommissionNumber() != null && grantMandatoryQuestions.getCharityCommissionNumber().length() > MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_MAX_LENGTH) {
            grantMandatoryQuestions.setCharityCommissionNumber(null);
        }
        if (grantMandatoryQuestions.getCompaniesHouseNumber() != null && grantMandatoryQuestions.getCompaniesHouseNumber().length() > MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_MAX_LENGTH) {
            grantMandatoryQuestions.setCompaniesHouseNumber(null);
        }

        grantMandatoryQuestions.setGrantScheme(scheme);
        grantMandatoryQuestions.setCreatedBy(applicant);

        return grantMandatoryQuestionRepository.save(grantMandatoryQuestions);
    }

    /**
     * Guarantees the given submission owns its own mandatory-questions record (copy-on-write), creating one if needed.
     * <p>
     * This is the single entry point for giving a submission its own MQ, covering both a brand-new multi-application
     * submission and a legacy submission that was sharing a sibling's record. Multi-application schemes must not share
     * a single MQ across submissions (doing so caused one submission's funding figures to overwrite another's).
     * <p>
     * If the submission already owns an MQ it is returned unchanged. Otherwise a new record is created: organisation
     * details are seeded from the submission's own definition when present (a legacy draft), falling back to the
     * applicant's most recent MQ for the scheme and then their organisation profile (a brand-new submission whose
     * definition has no organisation responses yet). Funding amount, funding location and gapId are always blanked so
     * each submission captures its own funding, and the blanked funding is projected back into the submission. A
     * funding section that was already COMPLETED (a healed draft) is reopened so it cannot be completed or submitted
     * until re-entered; a brand-new submission keeps its default funding-section status. The shared record is never
     * re-pointed. Submitted submissions are immutable and left untouched - those are handled by the separate
     * remediation job - as are version 1 schemes, which do not use MQ-backed sections.
     */
    public GrantMandatoryQuestions ensureMandatoryQuestionForSubmission(final UUID submissionId, final String applicantSub) {
        final Submission submission = submissionRepository.findByIdAndApplicantUserId(submissionId, applicantSub)
                .orElseThrow(() -> new NotFoundException(String.format("No submission with id %s was found for the current applicant", submissionId)));

        final Optional<GrantMandatoryQuestions> ownMandatoryQuestion = grantMandatoryQuestionRepository.findBySubmissionId(submissionId);
        if (ownMandatoryQuestion.isPresent()) {
            return ownMandatoryQuestion.get();
        }

        if (SubmissionStatus.SUBMITTED.equals(submission.getStatus()) || submission.getScheme().getVersion() <= 1) {
            return getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, applicantSub);
        }

        log.info("Submission {} has no mandatory question of its own; creating one and blanking its funding", submissionId);

        final GrantMandatoryQuestions perSubmissionMandatoryQuestion = buildPerSubmissionOrganisationDetails(submission, applicantSub);
        perSubmissionMandatoryQuestion.setStatus(GrantMandatoryQuestionStatus.IN_PROGRESS);
        perSubmissionMandatoryQuestion.setGapId(null);
        // Funding is never inherited: a borrowed sibling record may have overwritten this submission's funding, and a
        // brand-new submission must capture its own. Blank it so the applicant must (re)confirm funding for this
        // submission before its funding section can be completed or submitted.
        perSubmissionMandatoryQuestion.setFundingAmount(null);
        perSubmissionMandatoryQuestion.setFundingLocation(null);
        perSubmissionMandatoryQuestion.setGrantScheme(submission.getScheme());
        perSubmissionMandatoryQuestion.setCreatedBy(submission.getApplicant());
        perSubmissionMandatoryQuestion.setSubmission(submission);

        final GrantMandatoryQuestions savedMandatoryQuestion = grantMandatoryQuestionRepository.save(perSubmissionMandatoryQuestion);

        // Project the blanked funding into this submission's definition. Only reopen a funding section that was already
        // COMPLETED (a healed draft) so the section-completion / submit-readiness checks force re-entry; a brand-new
        // submission keeps its default funding-section status.
        addMandatoryQuestionsToSubmissionObject(savedMandatoryQuestion);
        final SubmissionSection fundingSection = submission.getSection(MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_ID);
        if (SubmissionSectionStatus.COMPLETED.equals(fundingSection.getSectionStatus())) {
            fundingSection.setSectionStatus(SubmissionSectionStatus.IN_PROGRESS);
        }
        submissionRepository.save(submission);

        return savedMandatoryQuestion;
    }

    /**
     * Builds the organisation details for a new per-submission mandatory question. An existing draft carries its
     * organisation details in its own definition, so they are seeded from there. A brand-new submission has no
     * organisation responses in its definition yet, so they are seeded from the applicant's most recent mandatory
     * question for the scheme (their previous submission), falling back to their organisation profile. Funding is
     * blanked by the caller in every case.
     */
    private GrantMandatoryQuestions buildPerSubmissionOrganisationDetails(final Submission submission, final String applicantSub) {
        final GrantMandatoryQuestions fromDefinition = buildMandatoryQuestionFromSubmissionDefinition(submission);
        if (fromDefinition.getOrgType() != null) {
            return fromDefinition;
        }

        final Integer schemeId = submission.getScheme().getId();
        return grantMandatoryQuestionRepository
                .findFirstByGrantScheme_IdAndCreatedBy_UserIdOrderByCreatedDesc(schemeId, applicantSub)
                .map(source -> GrantMandatoryQuestions.builder()
                        .orgType(source.getOrgType())
                        .name(source.getName())
                        .addressLine1(source.getAddressLine1())
                        .addressLine2(source.getAddressLine2())
                        .city(source.getCity())
                        .county(source.getCounty())
                        .postcode(source.getPostcode())
                        .companiesHouseNumber(source.getCompaniesHouseNumber())
                        .charityCommissionNumber(source.getCharityCommissionNumber())
                        .build())
                .orElseGet(() -> organisationProfileMapper
                        .mapOrgProfileToGrantMandatoryQuestion(submission.getApplicant().getOrganisationProfile()));
    }

    private GrantMandatoryQuestions buildMandatoryQuestionFromSubmissionDefinition(final Submission submission) {
        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder().build();

        findSubmissionQuestion(submission, MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString())
                .map(SubmissionQuestion::getResponse)
                .map(GrantMandatoryQuestionOrgType::valueOfName)
                .ifPresent(mandatoryQuestions::setOrgType);

        findSubmissionQuestion(submission, MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString())
                .map(SubmissionQuestion::getResponse)
                .ifPresent(mandatoryQuestions::setName);

        findSubmissionQuestion(submission, MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString())
                .map(SubmissionQuestion::getMultiResponse)
                .ifPresent(address -> {
                    mandatoryQuestions.setAddressLine1(elementOrNull(address, 0));
                    mandatoryQuestions.setAddressLine2(elementOrNull(address, 1));
                    mandatoryQuestions.setCity(elementOrNull(address, 2));
                    mandatoryQuestions.setCounty(elementOrNull(address, 3));
                    mandatoryQuestions.setPostcode(elementOrNull(address, 4));
                });

        findSubmissionQuestion(submission, MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString())
                .map(SubmissionQuestion::getResponse)
                .ifPresent(mandatoryQuestions::setCharityCommissionNumber);

        findSubmissionQuestion(submission, MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_COMPANIES_HOUSE.toString())
                .map(SubmissionQuestion::getResponse)
                .ifPresent(mandatoryQuestions::setCompaniesHouseNumber);

        findSubmissionQuestion(submission, MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString())
                .map(SubmissionQuestion::getResponse)
                .filter(response -> !response.isBlank())
                .map(BigDecimal::new)
                .ifPresent(mandatoryQuestions::setFundingAmount);

        findSubmissionQuestion(submission, MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_ID,
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.BENEFITIARY_LOCATION.toString())
                .map(SubmissionQuestion::getMultiResponse)
                .ifPresent(locations -> mandatoryQuestions.setFundingLocation(
                        Arrays.stream(locations)
                                .map(GrantMandatoryQuestionFundingLocation::valueOfName)
                                .filter(Objects::nonNull)
                                .toArray(GrantMandatoryQuestionFundingLocation[]::new)));

        return mandatoryQuestions;
    }

    private Optional<SubmissionQuestion> findSubmissionQuestion(final Submission submission, final String sectionId, final String questionId) {
        return submission.getDefinition().getSections().stream()
                .filter(section -> sectionId.equals(section.getSectionId()))
                .filter(section -> section.getQuestions() != null)
                .flatMap(section -> section.getQuestions().stream())
                .filter(question -> questionId.equals(question.getQuestionId()))
                .findFirst();
    }

    private static String elementOrNull(final String[] array, final int index) {
        return array != null && array.length > index ? array[index] : null;
    }


    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions, GrantApplicant grantApplicant) {
        if (grantMandatoryQuestions.getStatus().equals(GrantMandatoryQuestionStatus.COMPLETED)) {
            final String gapId = GapIdGenerator.generateGapId(grantApplicant.getId(), envProperties.getEnvironmentName(), grantMandatoryQuestionRepository.count(), 2);
            grantMandatoryQuestions.setGapId(gapId);
        }
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId()) //TODO there is no need for the additional database call here
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with id %s was found", grantMandatoryQuestions.getId())));
    }

    public String generateNextPageUrl(String url, UUID mandatoryQuestionId, String applicantSub) {
        final GrantMandatoryQuestions mqs = getGrantMandatoryQuestionById(mandatoryQuestionId, applicantSub);
        final Map<String, String> mapper = new HashMap<>();
        String mandatoryQuestionsUrlStart = "/mandatory-questions/" + mandatoryQuestionId;
        mapper.put("organisation-type", mandatoryQuestionsUrlStart + "/organisation-name");
        mapper.put("organisation-name", mandatoryQuestionsUrlStart + "/organisation-address");
        if (mqs.getOrgType() == GrantMandatoryQuestionOrgType.NON_LIMITED_COMPANY
                || mqs.getOrgType() == GrantMandatoryQuestionOrgType.INDIVIDUAL || mqs.getOrgType() == GrantMandatoryQuestionOrgType.LOCAL_AUTHORITY) {
            mapper.put("organisation-address", mandatoryQuestionsUrlStart + "/organisation-funding-amount");
        } else {
            mapper.put("organisation-address", mandatoryQuestionsUrlStart + "/organisation-companies-house-number");
            mapper.put("organisation-companies-house-number", mandatoryQuestionsUrlStart + "/organisation-charity-commission-number");
            mapper.put("organisation-charity-commission-number", mandatoryQuestionsUrlStart + "/organisation-funding-amount");
        }
        mapper.put("organisation-funding-amount", mandatoryQuestionsUrlStart + "/organisation-funding-location");
        mapper.put("organisation-funding-location", mandatoryQuestionsUrlStart + "/organisation-summary");

        final String[] urlParts = url.split("/");
        //takes the last part of the url and strips it of eventual queryParams
        final String questionPage = urlParts[urlParts.length - 1].split("\\?")[0];
        if (mapper.get(questionPage) == null) {
            log.info("No next page found for question page {}", questionPage);
            return "";
        }
        return mapper.get(questionPage);
    }

    public void addMandatoryQuestionsToSubmissionObject(final GrantMandatoryQuestions mandatoryQuestions) {
        final Submission submission = mandatoryQuestions.getSubmission();

        if (submission == null || submission.getScheme().getVersion() <= 1) {
            return;
        }

        // Each submission has its own mandatory-questions record, so the responses are applied only to
        // the submission linked to this MQ. There is deliberately NO cross-submission propagation:
        // updating one submission's MQ must never alter another submission's funding details.
        applyMandatoryQuestionsToSubmission(submission, mandatoryQuestions);
    }

    private void applyMandatoryQuestionsToSubmission(final Submission submission,
            final GrantMandatoryQuestions mandatoryQuestions) {
        log.info("Adding mandatory question responses to submission {}", submission.getId());

        final SubmissionSectionStatus organisationDetailsSectionStatus = submission.getSection("ORGANISATION_DETAILS")
                .getSectionStatus();
        final SubmissionSectionStatus fundingDetailsSectionStatus = submission.getSection("FUNDING_DETAILS")
                .getSectionStatus();

        final SubmissionSection updatedOrgDetails = buildOrganisationDetailsSubmissionSection(mandatoryQuestions,
                organisationDetailsSectionStatus);
        final SubmissionSection updatedFundingDetails = buildFundingDetailsSubmissionSection(mandatoryQuestions,
                fundingDetailsSectionStatus);

        submission.removeSection("ORGANISATION_DETAILS");
        submission.removeSection("FUNDING_DETAILS");

        /*
            Wouldn't usually access encapsulated collections this way, but I don't want to write
            a method on the Submission object to add a section which requires the array index to be specified
         */
        submission.getDefinition().getSections().add(1, updatedOrgDetails);
        submission.getDefinition().getSections().add(2, updatedFundingDetails);

        log.info(submission.getDefinition().toString());
    }

    public SubmissionSection buildOrganisationDetailsSubmissionSection(final GrantMandatoryQuestions mandatoryQuestions, final SubmissionSectionStatus sectionStatus) {
        final boolean isNonLimitedCompany = Objects.equals(mandatoryQuestions.getOrgType().toString(), GrantMandatoryQuestionOrgType.NON_LIMITED_COMPANY.toString());
        final boolean isIndividual = Objects.equals(mandatoryQuestions.getOrgType().toString(), GrantMandatoryQuestionOrgType.INDIVIDUAL.toString());
        final boolean isLocalAuthority = Objects.equals(mandatoryQuestions.getOrgType().toString(), GrantMandatoryQuestionOrgType.LOCAL_AUTHORITY.toString());
        final String sectionTitle = isIndividual
                ? MandatoryQuestionConstants.ORGANISATION_INDIVIDUAL_DETAILS_SECTION_TITLE
                : MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_TITLE;
        final SubmissionQuestion organisationName = mandatoryQuestionToSubmissionQuestion(
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString(),
                mandatoryQuestions
        );
        final SubmissionQuestion applicantType = mandatoryQuestionToSubmissionQuestion(
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString(),
                mandatoryQuestions
        );
        final SubmissionQuestion organisationAddress = mandatoryQuestionToSubmissionQuestion(
                MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString(),
                mandatoryQuestions
        );

        List<SubmissionQuestion> questions = new ArrayList<>();
        questions.add(applicantType);
        questions.add(organisationName);
        questions.add(organisationAddress);
        if (!isIndividual && !isNonLimitedCompany && !isLocalAuthority) {
            final SubmissionQuestion charityCommissionNumber = mandatoryQuestionToSubmissionQuestion(
                    MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString(),
                    mandatoryQuestions
            );
            final SubmissionQuestion companiesHouseNumber = mandatoryQuestionToSubmissionQuestion(
                    MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_COMPANIES_HOUSE.toString(),
                    mandatoryQuestions
            );
            questions.add(charityCommissionNumber);
            questions.add(companiesHouseNumber);
        }

        return SubmissionSection.builder()
                .sectionId(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID)
                .sectionTitle(sectionTitle)
                .questions(questions)
                .sectionStatus(sectionStatus)
                .build();
    }

    public SubmissionSection buildFundingDetailsSubmissionSection(final GrantMandatoryQuestions mandatoryQuestions, final SubmissionSectionStatus sectionStatus) {
        final SubmissionQuestion applicantAmount = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString(), mandatoryQuestions);
        final SubmissionQuestion beneficiaryLocation = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.BENEFITIARY_LOCATION.toString(), mandatoryQuestions);

        return SubmissionSection.builder()
                .sectionId(MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_ID)
                .sectionTitle(MandatoryQuestionConstants.FUNDING_DETAILS_SECTION_TITLE)
                .questions(List.of(
                        applicantAmount,
                        beneficiaryLocation
                ))
                .sectionStatus(sectionStatus)
                .build();
    }

    public SubmissionQuestion mandatoryQuestionToSubmissionQuestion(final String questionId, final GrantMandatoryQuestions mandatoryQuestions) {
        return switch (questionId) {
            case "APPLICANT_ORG_NAME" -> buildOrganisationNameQuestion(mandatoryQuestions);
            case "APPLICANT_TYPE" -> buildApplicationTypeQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_CHARITY_NUMBER" -> buildCharityCommissionNumberQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_COMPANIES_HOUSE" -> buildCompaniesHouseNumberQuestion(mandatoryQuestions);
            case "APPLICANT_AMOUNT" -> buildFundingAmountQuestion(mandatoryQuestions);
            case "BENEFITIARY_LOCATION" -> buildFundingLocationQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_ADDRESS" -> buildOrganisationAddressQuestion(mandatoryQuestions);
            default -> throw new IllegalArgumentException("There is no method to process this question");
        };
    }

    public boolean mandatoryQuestionExistsBySchemeIdAndApplicantId(Integer schemeId, Long applicantId) {
        return grantMandatoryQuestionRepository.existsByGrantScheme_IdAndCreatedBy_Id(schemeId, applicantId);
    }

    private SubmissionQuestion buildOrganisationNameQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .minLength(MandatoryQuestionConstants.APPLICANT_ORG_NAME_MIN_LENGTH)
                .maxLength(MandatoryQuestionConstants.APPLICANT_ORG_NAME_MAX_LENGTH)
                .build();

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString())
                .fieldTitle(MandatoryQuestionConstants.APPLICANT_SUBMISSION_ORG_NAME_TITLE)
                .profileField(MandatoryQuestionConstants.APPLICANT_ORG_NAME_PROFILE_FIELD)
                .hintText(MandatoryQuestionConstants.APPLICANT_ORG_NAME_HINT)
                .adminSummary(MandatoryQuestionConstants.APPLICANT_ORG_NAME_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.ShortAnswer)
                .response(mandatoryQuestions.getName())
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildApplicationTypeQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();

        final String title = Objects.equals(mandatoryQuestions.getOrgType().toString(), GrantMandatoryQuestionOrgType.INDIVIDUAL.toString())
                ? MandatoryQuestionConstants.APPLICANT_INDIVIDUAL_SUBMISSION_TYPE_TITLE
                : MandatoryQuestionConstants.APPLICANT_SUBMISSION_TYPE_TITLE;

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString())
                .fieldTitle(title)
                .profileField(MandatoryQuestionConstants.APPLICANT_TYPE_PROFILE_FIELD)
                .hintText(MandatoryQuestionConstants.APPLICANT_ORG_NAME_HINT)
                .adminSummary(MandatoryQuestionConstants.APPLICANT_TYPE_ADMIN_SUMMARY)
                .hintText(MandatoryQuestionConstants.APPLICANT_TYPE_HINT_TEXT)
                .responseType(SubmissionQuestionResponseType.Dropdown)
                .options(MandatoryQuestionConstants.APPLICANT_TYPE_OPTIONS)
                .response(mandatoryQuestions.getOrgType().toString())
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildCharityCommissionNumberQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(false)
                .minLength(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_MIN_LENGTH)
                .maxLength(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_MAX_LENGTH)
                .validInput(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_VALID_INPUT)
                .build();

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString())
                .fieldTitle(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_TITLE)
                .profileField(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_PROFILE_FIELD)
                .hintText(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_HINT_TEXT)
                .adminSummary(MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.ShortAnswer)
                .response(mandatoryQuestions.getCharityCommissionNumber())
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildCompaniesHouseNumberQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(false)
                .minLength(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_MIN_LENGTH)
                .maxLength(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_MAX_LENGTH)
                .validInput(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_VALID_INPUT)
                .build();

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_COMPANIES_HOUSE.toString())
                .fieldTitle(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_TITLE)
                .profileField(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_PROFILE_FIELD)
                .hintText(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_HINT_TEXT)
                .adminSummary(MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.ShortAnswer)
                .response(mandatoryQuestions.getCompaniesHouseNumber())
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildFundingAmountQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();

        final boolean isIndividual = Objects.equals(mandatoryQuestions.getOrgType().toString(),
                GrantMandatoryQuestionOrgType.INDIVIDUAL.toString());
        final String title = isIndividual
                ? MandatoryQuestionConstants.APPLICANT_INDIVIDUAL_AMOUNT_TITLE
                : MandatoryQuestionConstants.APPLICANT_AMOUNT_TITLE;

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString())
                .fieldTitle(title)
                .hintText(MandatoryQuestionConstants.APPLICANT_AMOUNT_HINT_TEXT)
                .adminSummary(MandatoryQuestionConstants.APPLICANT_AMOUNT_ADMIN_SUMMARY)
                .fieldPrefix(MandatoryQuestionConstants.APPLICANT_AMOUNT_PREFIX)
                .responseType(SubmissionQuestionResponseType.Numeric)
                .response(mandatoryQuestions.getFundingAmount() != null
                        ? mandatoryQuestions.getFundingAmount().toString()
                        : null)
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildFundingLocationQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final GrantMandatoryQuestionFundingLocation[] fundingLocations = mandatoryQuestions.getFundingLocation();
        final String[] locations = fundingLocations == null
                ? new String[0]
                : Arrays.stream(fundingLocations)
                        .map(GrantMandatoryQuestionFundingLocation::getName)
                        .toArray(String[]::new);

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.BENEFITIARY_LOCATION.toString())
                .fieldTitle(MandatoryQuestionConstants.APPLICANT_FUNDING_LOCATION_TITLE)
                .hintText(MandatoryQuestionConstants.APPLICANT_FUNDING_LOCATION_HINT_TEXT)
                .adminSummary(MandatoryQuestionConstants.APPLICANT_FUNDING_LOCATION_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.MultipleSelection)
                .options(MandatoryQuestionConstants.APPLICANT_FUNDING_LOCATION_OPTIONS)
                .multiResponse(locations)
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildOrganisationAddressQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        // TODO check whether other address to String array logic uses values directly or converts nulls to empty strings
        final String[] address = new String[]{
                mandatoryQuestions.getAddressLine1(),
                mandatoryQuestions.getAddressLine2(),
                mandatoryQuestions.getCity(),
                mandatoryQuestions.getCounty(),
                mandatoryQuestions.getPostcode()
        };

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString())
                .fieldTitle(MandatoryQuestionConstants.ORGANISATION_SUBMISSION_ADDRESS_TITLE)
                .profileField(MandatoryQuestionConstants.ORGANISATION_ADDRESS_PROFILE_FIELD)
                .adminSummary(MandatoryQuestionConstants.ORGANISATION_ADDRESS_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.AddressInput)
                .multiResponse(address)
                .validation(validation)
                .build();
    }

}
