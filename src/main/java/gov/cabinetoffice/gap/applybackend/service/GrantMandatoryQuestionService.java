package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.constants.MandatoryQuestionConstants;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantApplicantOrganisationProfileMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestionValidation;
import gov.cabinetoffice.gap.applybackend.model.SubmissionSection;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantMandatoryQuestionService {
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    private final GrantApplicantOrganisationProfileMapper organisationProfileMapper;

    public GrantMandatoryQuestions getGrantMandatoryQuestionById(UUID id, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", id))));

        if (grantMandatoryQuestion.isPresent() && !grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with ID %s was not created by %s", id, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions getGrantMandatoryQuestionBySubmissionId(UUID submissionId, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with submission id %s was found", submissionId))));

        if (!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with id % and submission ID %s was not created by %s", grantMandatoryQuestion.get().getId(), submissionId, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions getMandatoryQuestionByScheme(Integer schemeId, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository
                .findByGrantScheme_IdAndCreatedBy_UserId(schemeId, applicantSub)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with scheme id  %s was found", schemeId))));

        if (!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with id % and scheme ID %s was not created by %s",
                    grantMandatoryQuestion.get().getId(), schemeId, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions createMandatoryQuestion(GrantScheme scheme, GrantApplicant applicant) {
        if (existsBySchemeIdAndApplicantId(scheme.getId(), applicant.getId())) {
            log.debug("Mandatory question for scheme {}, and applicant {} already exist", scheme.getId(), applicant.getId());
            return grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).get(0);
        }

        final GrantApplicantOrganisationProfile organisationProfile = applicant.getOrganisationProfile();

        final GrantMandatoryQuestions grantMandatoryQuestions = organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(organisationProfile);

        //Fix to exclude any existing Charity Comission Number or Companies House Number which have invalid lengths,
        //This will force the applicant to go through the MQ journey and update their details with a valid length number
        if(grantMandatoryQuestions.getCharityCommissionNumber() != null && grantMandatoryQuestions.getCharityCommissionNumber().length() > MandatoryQuestionConstants.CHARITY_COMMISSION_NUMBER_MAX_LENGTH){
            grantMandatoryQuestions.setCharityCommissionNumber(null);
        }
        if(grantMandatoryQuestions.getCompaniesHouseNumber() != null && grantMandatoryQuestions.getCompaniesHouseNumber().length() > MandatoryQuestionConstants.COMPANIES_HOUSE_NUMBER_MAX_LENGTH){
            grantMandatoryQuestions.setCompaniesHouseNumber(null);
        }

        grantMandatoryQuestions.setGrantScheme(scheme);
        grantMandatoryQuestions.setCreatedBy(applicant);

        return grantMandatoryQuestionRepository.save(grantMandatoryQuestions);
    }


    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions) {
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId()) //TODO there is no need for the additional database call here
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", grantMandatoryQuestions.getId())));
    }


    public String generateNextPageUrl(String url, UUID mandatoryQuestionId) {
        final Map<String, String> mapper = new HashMap<>();
        String mandatoryQuestionsUrlStart = "/mandatory-questions/" + mandatoryQuestionId;
        mapper.put("organisation-name", mandatoryQuestionsUrlStart + "/organisation-address");
        mapper.put("organisation-address", mandatoryQuestionsUrlStart + "/organisation-type");
        mapper.put("organisation-type", mandatoryQuestionsUrlStart + "/organisation-companies-house-number");
        mapper.put("organisation-companies-house-number", mandatoryQuestionsUrlStart + "/organisation-charity-commission-number");
        mapper.put("organisation-charity-commission-number", mandatoryQuestionsUrlStart + "/organisation-funding-amount");
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
        if (mandatoryQuestions.getSubmission() != null &&
                mandatoryQuestions.getSubmission().getVersion() > 1) {
            final Submission submission = mandatoryQuestions.getSubmission();

            log.info("Adding mandatory question responses to submission " + submission.getId());

            final SubmissionSectionStatus organisationDetailsSectionStatus = submission.getSection("ORGANISATION_DETAILS").getSectionStatus();
            final SubmissionSectionStatus fundingDetailsSectionStatus = submission.getSection("FUNDING_DETAILS").getSectionStatus();

            final SubmissionSection updatedOrgDetails = buildOrganisationDetailsSubmissionSection(mandatoryQuestions, organisationDetailsSectionStatus);
            final SubmissionSection updatedFundingDetails = buildFundingDetailsSubmissionSection(mandatoryQuestions, fundingDetailsSectionStatus);

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
    }

    public SubmissionSection buildOrganisationDetailsSubmissionSection(final GrantMandatoryQuestions mandatoryQuestions, final SubmissionSectionStatus sectionStatus) {
        final SubmissionQuestion organisationName = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_NAME.toString(), mandatoryQuestions);
        final SubmissionQuestion applicantType = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString(), mandatoryQuestions);
        final SubmissionQuestion organisationAddress = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_ADDRESS.toString(), mandatoryQuestions);
        final SubmissionQuestion charityCommissionNumber = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_CHARITY_NUMBER.toString(), mandatoryQuestions);
        final SubmissionQuestion companiesHouseNumber = mandatoryQuestionToSubmissionQuestion(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_ORG_COMPANIES_HOUSE.toString(), mandatoryQuestions);

        return SubmissionSection.builder()
                .sectionId(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_ID)
                .sectionTitle(MandatoryQuestionConstants.ORGANISATION_DETAILS_SECTION_TITLE)
                .questions(List.of(
                        organisationName,
                        applicantType,
                        organisationAddress,
                        charityCommissionNumber,
                        companiesHouseNumber
                ))
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
        switch (questionId) {
            case "APPLICANT_ORG_NAME":
                return buildOrganisationNameQuestion(mandatoryQuestions);
            case "APPLICANT_TYPE":
                return buildApplicationTypeQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_CHARITY_NUMBER":
                return buildCharityCommissionNumberQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_COMPANIES_HOUSE":
                return buildCompaniesHouseNumberQuestion(mandatoryQuestions);
            case "APPLICANT_AMOUNT":
                return buildFundingAmountQuestion(mandatoryQuestions);
            case "BENEFITIARY_LOCATION":
                return buildFundingLocationQuestion(mandatoryQuestions);
            case "APPLICANT_ORG_ADDRESS":
                return buildOrganisationAddressQuestion(mandatoryQuestions);
            default:
                throw new IllegalArgumentException("There is no method to process this question");
        }
    }

    public boolean existsBySchemeIdAndApplicantId(Integer schemeId, Long applicantId) {
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
                .fieldTitle(MandatoryQuestionConstants.APPLICANT_ORG_NAME_TITLE)
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

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_TYPE.toString())
                .fieldTitle(MandatoryQuestionConstants.APPLICANT_TYPE_TITLE)
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

        return SubmissionQuestion.builder()
                .questionId(MandatoryQuestionConstants.SUBMISSION_QUESTION_IDS.APPLICANT_AMOUNT.toString())
                .fieldTitle(MandatoryQuestionConstants.APPLICANT_AMOUNT_TITLE)
                .hintText(MandatoryQuestionConstants.APPLICANT_AMOUNT_HINT_TEXT)
                .adminSummary(MandatoryQuestionConstants.APPLICANT_AMOUNT_ADMIN_SUMMARY)
                .fieldPrefix(MandatoryQuestionConstants.APPLICANT_AMOUNT_PREFIX)
                .responseType(SubmissionQuestionResponseType.Numeric)
                .response(mandatoryQuestions.getFundingAmount().toString())
                .validation(validation)
                .build();
    }

    private SubmissionQuestion buildFundingLocationQuestion(final GrantMandatoryQuestions mandatoryQuestions) {
        final String[] locations = Arrays.stream(mandatoryQuestions.getFundingLocation())
                .map(location -> location.getName())
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
                .fieldTitle(MandatoryQuestionConstants.ORGANISATION_ADDRESS_TITLE)
                .profileField(MandatoryQuestionConstants.ORGANISATION_ADDRESS_PROFILE_FIELD)
                .adminSummary(MandatoryQuestionConstants.ORGANISATION_ADDRESS_ADMIN_SUMMARY)
                .responseType(SubmissionQuestionResponseType.AddressInput)
                .multiResponse(address)
                .validation(validation)
                .build();
    }


}
