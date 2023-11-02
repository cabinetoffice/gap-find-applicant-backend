package gov.cabinetoffice.gap.applybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cabinetoffice.gap.applybackend.client.GovNotifyClient;
import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.constants.APIConstants;
import gov.cabinetoffice.gap.applybackend.dto.api.CreateQuestionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.CreateSubmissionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetNavigationParamsDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.exception.SubmissionAlreadySubmittedException;
import gov.cabinetoffice.gap.applybackend.exception.SubmissionNotReadyException;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.repository.DiligenceCheckRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class SubmissionService {
    private static final String ESSENTIAL_SECTION_ID = "ESSENTIAL";
    private static final String APPLICANT_ORG_NAME = "APPLICANT_ORG_NAME";
    private static final String APPLICANT_ORG_TYPE = "APPLICANT_ORG_TYPE";
    private static final String APPLICANT_ORG_ADDRESS = "APPLICANT_ORG_ADDRESS";
    private static final String APPLICANT_AMOUNT = "APPLICANT_AMOUNT";
    private static final String APPLICANT_ORG_COMPANIES_HOUSE = "APPLICANT_ORG_COMPANIES_HOUSE";
    private static final String APPLICANT_ORG_CHARITY_NUMBER = "APPLICANT_ORG_CHARITY_NUMBER";
    private static final String BENEFITIARY_LOCATION = "BENEFITIARY_LOCATION";
    private static final String ELIGIBILITY = "ELIGIBILITY";
    public static final String ORGANISATION_DETAILS = "ORGANISATION_DETAILS";
    public static final String FUNDING_DETAILS = "FUNDING_DETAILS";


    private final SubmissionRepository submissionRepository;
    private final DiligenceCheckRepository diligenceCheckRepository;
    private final GrantBeneficiaryRepository grantBeneficiaryRepository;
    private final GovNotifyClient notifyClient;

    private final Clock clock;
    private final EnvironmentProperties envProperties;

    public Submission getSubmissionFromDatabaseBySubmissionId(final String userId, final UUID submissionId) {
        Submission submission = submissionRepository
                .findByIdAndApplicantUserId(submissionId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No Submission with ID %s was found", submissionId)));

        // Don't love this. Begging for exceptions to be thrown.
        if (submission.getApplication().getGrantScheme().getVersion() == 1) {
            populateEssentialInformation(userId, submission);
        }

        return submission;
    }

    public SubmissionSection getSectionBySectionId(final String userId, final UUID submissionId, String sectionId) {
        return submissionRepository
                .findByIdAndApplicantUserId(submissionId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No Submission with ID %s was found", submissionId)))
                .getDefinition()
                .getSections()
                .stream().filter(section -> section.getSectionId().equals(sectionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(
                        String.format("No Section with ID %s was found", sectionId)));
    }

    public SubmissionQuestion getQuestionByQuestionId(final String userId, final UUID submissionId, String questionId) {
        return this.getSubmissionFromDatabaseBySubmissionId(userId, submissionId)
                .getDefinition()
                .getSections()
                .stream()
                .flatMap(sections -> sections.getQuestions().stream())
                .filter(q -> q.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(
                        String.format("No question with ID %s was found", questionId)));
    }

    public Submission saveSubmission(final Submission submission) {
        return this.submissionRepository.save(submission);
    }

    public void saveQuestionResponse(final CreateQuestionResponseDto questionResponse, final String userId, final UUID submissionId, final String sectionId) {

        final Submission submission = this.getSubmissionFromDatabaseBySubmissionId(userId, submissionId);
        final SubmissionSection submissionSection = submission.getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("No section with ID %s was found", sectionId)));

        final SubmissionQuestion submissionQuestion = submissionSection.getQuestions()
                .stream()
                .filter(question -> question.getQuestionId().equals(questionResponse.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("No question with ID %s was found", questionResponse.getQuestionId())));

        if (questionResponse.getResponse() != null) {
            submissionQuestion.setResponse(questionResponse.getResponse());
            submissionSection.setSectionStatus(SubmissionSectionStatus.IN_PROGRESS);
        }

        if (questionResponse.getMultiResponse() != null) {
            submissionQuestion.setMultiResponse(questionResponse.getMultiResponse());
            submissionSection.setSectionStatus(SubmissionSectionStatus.IN_PROGRESS);
        }

        if (sectionId.equals(ELIGIBILITY)) {
            Stream<SubmissionSection> nonEligibilitySections = submission.getDefinition()
                    .getSections()
                    .stream()
                    .filter(section -> !section.getSectionId().equals(ELIGIBILITY));

            if (questionResponse.getResponse().equals("Yes")) {
                nonEligibilitySections
                        .filter(section -> section.getSectionStatus().equals(SubmissionSectionStatus.CANNOT_START_YET))
                        .forEach(section -> section.setSectionStatus(SubmissionSectionStatus.NOT_STARTED));
            } else {
                nonEligibilitySections
                        .filter(section -> section.getSectionStatus().equals(SubmissionSectionStatus.NOT_STARTED))
                        .forEach(section -> section.setSectionStatus(SubmissionSectionStatus.CANNOT_START_YET));
            }
        }
        submissionRepository.save(submission);
    }

    public GetNavigationParamsDto getNextNavigation(final String userId, final UUID submissionId, final String sectionId, final String questionId, final boolean saveAndExit) {

        final SubmissionSection section = this.getSectionBySectionId(userId, submissionId, sectionId);
        final Map<String, Object> nextNavigation = this.buildNextNavigationMap(section, questionId, saveAndExit);

        return GetNavigationParamsDto.builder()
                .responseAccepted(Boolean.TRUE)
                .nextNavigation(nextNavigation)
                .build();
    }

    private Map<String, Object> buildNextNavigationMap(SubmissionSection section, String questionId,
                                                       boolean saveAndExit) {
        final Optional<String> nextQuestionId = this.getNextQuestionIdInSection(section, questionId);
        if (nextQuestionId.isPresent() && !saveAndExit) {
            return Map.of(
                    APIConstants.NAVIGATION_SECTION_ID, section.getSectionId(),
                    APIConstants.NAVIGATION_QUESTION_ID, nextQuestionId.get());
        } else {
            return Map.of(APIConstants.NAVIGATION_SECTION_LIST, Boolean.TRUE);
        }
    }

    private Optional<String> getNextQuestionIdInSection(SubmissionSection section, String questionId) {
        Optional<String> nextQuestionId = Optional.empty();

        // last question in the list can't have a next question and this case is
        // implicitly handled with the default value of nextQuestionId above
        for (int i = 0; i < section.getQuestions().size() - 1; i++) {
            SubmissionQuestion q = section.getQuestions().get(i);
            if (q.getQuestionId().equals(questionId)) {
                nextQuestionId = Optional.of(section.getQuestions().get(i + 1).getQuestionId());
                break;
            }
        }

        return nextQuestionId;
    }

    public boolean isSubmissionReadyToBeSubmitted(final String userId, final UUID submissionId) {
        final Submission submission = getSubmissionFromDatabaseBySubmissionId(userId, submissionId);
        GrantApplication grantApplication = submission.getApplication();
        if (!grantApplication.getApplicationStatus().equals(GrantApplicantStatus.PUBLISHED)) {
            return false;
        }

        return submission
                .getDefinition()
                .getSections()
                .stream()
                .flatMap(section -> section.getQuestions().stream())
                .filter(question -> {
                    boolean isMandatory = question.getValidation().isMandatory();
                    boolean responseIsEmptyOrNull = question.getResponse() == null
                            || question.getResponse().isBlank();
                    boolean multiResponseArrayIsEmptyOrNull = question.getMultiResponse() == null
                            || question.getMultiResponse().length < 1;
                    return isMandatory
                            && (responseIsEmptyOrNull && multiResponseArrayIsEmptyOrNull);
                })
                .toList()
                .isEmpty();
    }

    @Transactional
    public void submit(final Submission submission, final GrantApplicant grantApplicant, final String emailAddress) {

        if (!isSubmissionReadyToBeSubmitted(grantApplicant.getUserId(), submission.getId())) {
            throw new SubmissionNotReadyException(String
                    .format("Submission %s is not ready to be submitted.", submission.getId()));
        }

        if (submission.getStatus().equals(SubmissionStatus.SUBMITTED)) {
            throw new SubmissionAlreadySubmittedException(
                    String.format("Submission %s has already been submitted.", submission.getId()));
        }

        submission.setGapId(generateGapId(grantApplicant.getId()));
        submitApplication(submission);
        notifyClient.sendConfirmationEmail(emailAddress, submission);
        createDiligenceCheckFromSubmission(submission);
        createGrantBeneficiary(submission);
    }

    private void submitApplication(final Submission submission) {
        submission.setStatus(SubmissionStatus.SUBMITTED);

        submission.getDefinition().getSections()
                .forEach(section -> section.setSectionStatus(SubmissionSectionStatus.COMPLETED));
        submission.setSubmittedDate(ZonedDateTime.now(clock));

        submissionRepository.save(submission);
    }

    private void createDiligenceCheckFromSubmission(final Submission submission) {
        final String organisationName = getQuestionResponseByQuestionId(submission, APPLICANT_ORG_NAME);
        final String[] organisationAddress = getQuestionMultiResponseByQuestionId(submission, APPLICANT_ORG_ADDRESS);
        final String applicationAmount = getQuestionResponseByQuestionId(submission, APPLICANT_AMOUNT);
        final String companiesHouseNumber = getQuestionResponseByQuestionId(submission, APPLICANT_ORG_COMPANIES_HOUSE);
        final String charitiesCommissionNumber = getQuestionResponseByQuestionId(submission, APPLICANT_ORG_CHARITY_NUMBER);

        diligenceCheckRepository.save(DiligenceCheck.builder()
                .submissionId(submission.getId())
                .applicationNumber(submission.getGapId())
                .organisationName(organisationName)
                .addressStreet(organisationAddress[0])
                .addressTown(organisationAddress[2])
                .addressCounty(organisationAddress[3])
                .addressPostcode(organisationAddress[4])
                .applicationAmount(applicationAmount)
                .companiesHouseNumber(companiesHouseNumber)
                .charityNumber(charitiesCommissionNumber)
                .build());
    }

    private String getQuestionResponseByQuestionId(final Submission submission, final String questionId) {
        return submission.getDefinition().getSections()
                .stream()
                .flatMap(s -> s.getQuestions().stream())
                .filter(question -> question.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("submission %s does not contain a question with an ID of %s",
                                submission.getId(), questionId)))
                .getResponse();
    }

    private String[] getQuestionMultiResponseByQuestionId(final Submission submission, final String questionId) {
        return submission.getDefinition().getSections()
                .stream()
                .flatMap(s -> s.getQuestions().stream())
                .filter(question -> question.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("submission %s does not contain a question with an ID of %s",
                                submission.getId(), questionId)))
                .getMultiResponse();
    }

    private String[] getMultiResponseBySectionAndQuestionId(final SubmissionSection essentialInfoSection,
                                                            final String questionId) {
        return essentialInfoSection.getQuestions()
                .stream()
                .filter(question -> question.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("section %s does not contain a question with an ID of %s",
                                essentialInfoSection.getSectionId(), questionId)))
                .getMultiResponse();
    }

    private String generateGapId(final Long userId) {
        final String env = envProperties.getEnvironmentName();
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String diligenceCheckDate = currentDate.format(formatter);

        final long diligenceCheckRecordsFromToday = diligenceCheckRepository
                .countDistinctByApplicationNumberContains(diligenceCheckDate);

        final long diligenceRecordNumber = diligenceCheckRecordsFromToday + 1;
        return "GAP" +
                "-" +
                env +
                "-" +
                diligenceCheckDate +
                "-" +
                diligenceRecordNumber +
                "-" +
                userId
                ;
    }

    private void createGrantBeneficiary(final Submission submission) {
        final String[] locations = getQuestionMultiResponseByQuestionId(submission, BENEFITIARY_LOCATION);

        grantBeneficiaryRepository.save(GrantBeneficiary.builder()
                .schemeId(submission.getScheme().getId())
                .applicationId(submission.getApplication().getId())
                .submissionId(submission.getId())
                .createdBy(submission.getApplicant().getId())
                .locationNeEng(containsLocation(locations, "North East England"))
                .locationNwEng(containsLocation(locations, "North West England"))
                .locationSeEng(containsLocation(locations, "South East England"))
                .locationSwEng(containsLocation(locations, "South West England"))
                .locationMidEng(containsLocation(locations, "Midlands"))
                .locationSco(containsLocation(locations, "Scotland"))
                .locationWal(containsLocation(locations, "Wales"))
                .locationNir(containsLocation(locations, "Northern Ireland"))
                .gapId(submission.getGapId())
                .build());
    }

    private boolean containsLocation(String[] locations, String locationToFind) {
        return Arrays.asList(locations).contains(locationToFind);
    }

    public boolean hasSubmissionBeenSubmitted(final String userId, final UUID submissionId) {
        return !this.getSubmissionFromDatabaseBySubmissionId(userId, submissionId)
                .getStatus().equals(SubmissionStatus.IN_PROGRESS);
    }


    public boolean doesSubmissionExist(GrantApplicant grantApplicant, GrantApplication grantApplication) {
        return submissionRepository.findByApplicantId(grantApplicant.getId())
                .stream()
                .anyMatch(submission -> submission.getApplication().getId().equals(grantApplication.getId()));
    }

    public Optional<Submission> getSubmissionByApplicantAndApplicationId(GrantApplicant grantApplicant,
                                                                         GrantApplication grantApplication) {
        return submissionRepository.findByApplicantIdAndApplicationId(grantApplicant.getId(), grantApplication.getId());
    }


    public CreateSubmissionResponseDto createSubmissionFromApplication(final String userId,
                                                                       final GrantApplicant grantApplicant,
                                                                       final GrantApplication grantApplication) throws JsonProcessingException {
        final GrantScheme grantScheme = grantApplication.getGrantScheme();
        final int version = grantApplication.getVersion();
        final String applicationName = grantApplication.getApplicationName();
        final SubmissionDefinition definition = this.transformApplicationDefinitionToSubmissionDefinition(grantApplication.getDefinition(), grantScheme.getVersion());

        final LocalDateTime now = LocalDateTime.now(clock);

        final Submission newSubmission = Submission.builder()
                .scheme(grantScheme)
                .application(grantApplication)
                .applicant(grantApplicant)
                .version(version)
                .created(now)
                .createdBy(grantApplicant)
                .lastUpdated(now)
                .lastUpdatedBy(grantApplicant)
                .applicationName(applicationName)
                .status(SubmissionStatus.IN_PROGRESS)
                .definition(definition)
                .build();

        final Submission submission = submissionRepository.save(newSubmission);
        final UUID submissionId = submission.getId();
        final CreateSubmissionResponseDto submissionResponseDto = CreateSubmissionResponseDto.builder()
                .submissionCreated(true)
                .submissionId(submissionId)
                .build();

        if (grantScheme.getVersion() == 1) {
            populateEssentialInformation(userId, submission);
        }

        return submissionResponseDto;
    }

    private void populateEssentialInformation(final String userId, final Submission submission) {
        GrantApplicantOrganisationProfile grantApplicantOrgProfile = submission.getApplicant().getOrganisationProfile();

        if (grantApplicantOrgProfile != null) {

            final SubmissionSection section = getSectionBySectionId(userId, submission.getId(), ESSENTIAL_SECTION_ID);
            for (SubmissionQuestion question : section.getQuestions()) {
                if (question.getQuestionId().equals(APPLICANT_ORG_ADDRESS)) {
                    getMultiResponseForEssentialInfo(question, section, grantApplicantOrgProfile);
                } else {
                    final String organisationDefault = switch (question.getQuestionId()) {
                        case APPLICANT_ORG_NAME -> grantApplicantOrgProfile.getLegalName();
                        case APPLICANT_ORG_TYPE ->
                                grantApplicantOrgProfile.getType() != null ? grantApplicantOrgProfile.getType().toString() : null;
                        case APPLICANT_ORG_CHARITY_NUMBER -> grantApplicantOrgProfile.getCharityCommissionNumber();
                        case APPLICANT_ORG_COMPANIES_HOUSE -> grantApplicantOrgProfile.getCompaniesHouseNumber();
                        default -> null;
                    };

                    if (question.getResponse() == null && organisationDefault != null) {
                        question.setResponse(organisationDefault);
                    }
                }
            }

            submissionRepository.save(submission);
        }
    }

    public void deleteQuestionResponse(final String userId, final UUID submissionId, final String questionId) {
        final Submission submission = this.getSubmissionFromDatabaseBySubmissionId(userId, submissionId);
        submission.getDefinition()
                .getSections()
                .stream()
                .flatMap(sections -> sections.getQuestions().stream())
                .filter(q -> q.getQuestionId().equals(questionId))
                .findAny()
                .ifPresentOrElse(
                        q -> {
                            q.setResponse(null);
                            q.setMultiResponse(null);
                            q.setAttachmentId(null);
                        },
                        () -> {
                            throw new NotFoundException(String.format("No question with ID %s was found", questionId));
                        });

        submissionRepository.save(submission);
    }

    private void getMultiResponseForEssentialInfo(SubmissionQuestion question, SubmissionSection section,
                                                  GrantApplicantOrganisationProfile grantApplicantOrgProfile) {
        String[] response = getMultiResponseBySectionAndQuestionId(section, APPLICANT_ORG_ADDRESS);
        String[] address = {grantApplicantOrgProfile.getAddressLine1(),
                grantApplicantOrgProfile.getAddressLine2(), grantApplicantOrgProfile.getTown(),
                grantApplicantOrgProfile.getCounty(), grantApplicantOrgProfile.getPostcode()};

        if (response == null) {
            question.setMultiResponse(address);
        } else {
            for (int i = 0; i < response.length; i++) {
                if (response[i] == null || response[i].length() <= 0)
                    response[i] = address[i];
            }
            question.setMultiResponse(response);
        }
    }


    public SubmissionSectionStatus handleSectionReview(final String userId,
                                                       final UUID submissionId,
                                                       final String sectionId,
                                                       final boolean isComplete) {
        final Submission submission = getSubmissionFromDatabaseBySubmissionId(userId, submissionId);
        final SubmissionSectionStatus sectionStatus = isComplete ? SubmissionSectionStatus.COMPLETED : SubmissionSectionStatus.IN_PROGRESS;
        submission.getDefinition()
                .getSections()
                .stream()
                .filter(submissionSection -> submissionSection.getSectionId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("No section with ID %s was found", sectionId)))
                .setSectionStatus(sectionStatus);
        submissionRepository.save(submission);
        return sectionStatus;
    }

    public SubmissionDefinition transformApplicationDefinitionToSubmissionDefinition(ApplicationDefinition applicationDefinition, int schemeVersion) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String applicationDefinitionJson = objectMapper.writeValueAsString(applicationDefinition);
        final SubmissionDefinition definition = objectMapper.readValue(applicationDefinitionJson, SubmissionDefinition.class);

        // If scheme version is > 1 then the ESSENTIAL section needs to be split into 2 new sections: ORGANISATION_DETAILS and FUNDING_DETAILS
        // At this stage it's fine if the Question arrays are empty since we'll be re-building them based on mandatory question responses.
        if (schemeVersion > 1) {
            definition.getSections().removeIf(section -> section.getSectionId().equals(ESSENTIAL_SECTION_ID));
            definition.getSections().add(1,
                    SubmissionSection.builder()
                    .sectionId(ORGANISATION_DETAILS)
                    .build()
            );
            definition.getSections().add(2,
                    SubmissionSection.builder()
                            .sectionId(FUNDING_DETAILS)
                            .build()
            );
        }


        definition.getSections().forEach(section -> {
                    final SubmissionSectionStatus status = section.getSectionId().equals(ELIGIBILITY) ? SubmissionSectionStatus.NOT_STARTED : SubmissionSectionStatus.CANNOT_START_YET;
                    section.setSectionStatus(status);
                }
        );

        return definition;
    }
}

