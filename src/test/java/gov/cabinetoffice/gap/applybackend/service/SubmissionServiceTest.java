package gov.cabinetoffice.gap.applybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import gov.cabinetoffice.gap.applybackend.provider.UuidProvider;
import gov.cabinetoffice.gap.applybackend.repository.DiligenceCheckRepository;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import gov.cabinetoffice.gap.applybackend.repository.SubmissionRepository;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    private final String CHRISTMAS_2022_MIDDAY = "2022-12-25T12:00:00.00z";
    private final Clock clock = Clock.fixed(Instant.parse(CHRISTMAS_2022_MIDDAY), ZoneId.of("UTC"));

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private DiligenceCheckRepository diligenceCheckRepository;

    @Mock
    private GrantBeneficiaryRepository grantBeneficiaryRepository;

    @Mock
    private UuidProvider uuidProvider;

    @Mock
    private GovNotifyClient notifyClient;


    private SubmissionService serviceUnderTest;

    private final UUID SUBMISSION_ID = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");
    final LocalDateTime timestamp = LocalDateTime.now(clock);
    final Instant instant = Instant.now(clock);

    final String SECTION_ID_1 = "ESSENTIAL";
    final String SECTION_ID_SECTION_CANNOT_START_YET = "SECTION_CANNOT_START_YET";
    final String SECTION_ID_NOT_STARTED = "SECTION_NOT_STARTED";
    final String QUESTION_ID = "ORG_ADDRESS";

    final String orgName = "AND Digital";
    final String[] addressResponse = new String[]{"AND Digital", "9 George Square", "Glasgow", "", "G2 1QQ"};
    final String amount = "1000";
    final String companiesHouseNo = "1234";
    final String charityNo = "1234";
    final String[] beneficiaryLocation = new String[]{"South West England", "Midlands", "Scotland"};
    final String userId = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";

    private SubmissionQuestion question;
    private SubmissionSection section;
    private Submission submission;

    private GrantApplication grantApplication;

    private static Stream<Arguments> provideSectionParams() {
        return Stream.of(
                // submission ID: 5, section ID: 4, question ID: 1
                Arguments.of(
                        UUID.randomUUID(), "4", "1", false, Map.of(
                                APIConstants.NAVIGATION_SECTION_ID, "4",
                                APIConstants.NAVIGATION_QUESTION_ID, "2")),

                // submission ID: 5, section ID: 4, question ID: 2
                Arguments.of(
                        UUID.randomUUID(), "4", "2", false, Map.of(
                                APIConstants.NAVIGATION_SECTION_ID, "4",
                                APIConstants.NAVIGATION_QUESTION_ID, "3")),

                // submission ID: 5, section ID: 4, question ID: 3
                Arguments.of(
                        UUID.randomUUID(), "4", "3", false, Map.of(
                                APIConstants.NAVIGATION_SECTION_LIST, Boolean.TRUE)),

                // submission ID: 5, section ID: 4, question ID: 2
                Arguments.of(
                        UUID.randomUUID(), "4", "2", true, Map.of(
                                APIConstants.NAVIGATION_SECTION_LIST, Boolean.TRUE)));
    }

    private static Stream<Arguments> provideSectionsWithMissingQuestions() {
        return Stream.of(
                // should throw the exception on the getResponseBySectionAndQuestionId method
                Arguments.of(Collections.emptyList()),

                // should throw the exception on the getMultiResponseBySectionAndQuestionId
                // method
                Arguments.of(List.of(
                        SubmissionQuestion.builder()
                                .questionId("APPLICANT_ORG_NAME")
                                .response("")
                                .build())));
    }

    @BeforeEach
    void setup() {

        EnvironmentProperties envProperties = EnvironmentProperties.builder()
                .environmentName("LOCAL")
                .build();

        serviceUnderTest = Mockito.spy(new SubmissionService(submissionRepository, diligenceCheckRepository,
                grantBeneficiaryRepository, notifyClient, clock,
                uuidProvider, envProperties));

        question = SubmissionQuestion.builder()
                .questionId(QUESTION_ID)
                .build();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();
        final SubmissionQuestion orgNameQuestion = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_NAME")
                .response(orgName)
                .validation(validation)
                .build();

        final SubmissionQuestion orgAddressQuestion = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_ADDRESS")
                .multiResponse(addressResponse)
                .validation(validation)
                .build();

        final SubmissionQuestion amountQuestion = SubmissionQuestion.builder()
                .questionId("APPLICANT_AMOUNT")
                .response(amount)
                .validation(validation)
                .build();
        final SubmissionQuestion eligibilityQuestion = SubmissionQuestion.builder()
                .questionId("ELIGIBILITY")
                .response("Yes")
                .validation(validation)
                .build();

        final SubmissionQuestion companiesHouseQuestion = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_COMPANIES_HOUSE")
                .response(companiesHouseNo)
                .validation(validation)
                .build();

        final SubmissionQuestion charitiesCommissionQuestion = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_CHARITY_NUMBER")
                .response(charityNo)
                .validation(validation)
                .build();

        final SubmissionQuestion beneficiaryLocationQuestion = SubmissionQuestion.builder()
                .questionId("BENEFITIARY_LOCATION")
                .multiResponse(beneficiaryLocation)
                .validation(validation)
                .build();

        section = SubmissionSection.builder()
                .sectionId("ESSENTIAL")
                .sectionStatus(SubmissionSectionStatus.IN_PROGRESS)
                .questions(List.of(orgNameQuestion, orgAddressQuestion, amountQuestion,
                        companiesHouseQuestion,
                        charitiesCommissionQuestion, beneficiaryLocationQuestion, question))
                .build();

        SubmissionSection sectionCannotStartYet = SubmissionSection.builder()
                .sectionId("SECTION_CANNOT_START_YET")
                .sectionStatus(SubmissionSectionStatus.CANNOT_START_YET)
                .questions(List.of(orgNameQuestion, orgAddressQuestion, amountQuestion, companiesHouseQuestion,
                        charitiesCommissionQuestion, question))
                .build();

        SubmissionSection sectionNotStarted = SubmissionSection.builder()
                .sectionId("SECTION_NOT_STARTED")
                .sectionStatus(SubmissionSectionStatus.NOT_STARTED)
                .questions(List.of(orgNameQuestion, orgAddressQuestion, amountQuestion, companiesHouseQuestion,
                        charitiesCommissionQuestion, question))
                .build();

        SubmissionSection eligibilitySection = SubmissionSection.builder()
                .sectionId("ELIGIBILITY")
                .sectionStatus(SubmissionSectionStatus.IN_PROGRESS)
                .questions(List.of(eligibilityQuestion))
                .build();

        SubmissionDefinition definition = SubmissionDefinition.builder()
                .sections(List.of(section, eligibilitySection, sectionNotStarted, sectionCannotStartYet))
                .build();

        final GrantApplicant grantApplicant = GrantApplicant.builder().id(1)
                .userId(userId)
                .build();

        final GrantScheme grantScheme = GrantScheme.builder()
                .id(1)
                .funderId(1)
                .version(1)
                .lastUpdated(instant)
                .lastUpdatedBy(1)
                .ggisIdentifier("SCH-000003589")
                .name("scheme_name")
                .email("contact@contact.com")
                .build();

        grantApplication = GrantApplication.builder()
                .id(1)
                .applicationName("Test Application")
                .created(instant)
                .lastUpdated(instant)
                .definition(new ApplicationDefinition())
                .grantScheme(grantScheme)
                .version(1)
                .lastUpdateBy(1)
                .build();

        submission = Submission.builder()
                .id(SUBMISSION_ID)
                .version(1)
                .gapId("GAP-LL-aDate-1")
                .applicant(grantApplicant)
                .scheme(grantScheme)
                .application(grantApplication)
                .created(timestamp)
                .lastUpdated(timestamp)
                .applicationName("Test Submission")
                .status(SubmissionStatus.IN_PROGRESS)
                .definition(definition)
                .build();
    }

    @Test
    void getSubmissionsByApplicantId_ReturnsExpectedResult() {
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

        Submission methodResponse = serviceUnderTest.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        verify(submissionRepository).findById(SUBMISSION_ID);
        assertEquals(methodResponse, submission);
    }

    @Test
    void getSubmissionsByApplicantId_ReturnsExpectedResultWithEssentialInfoPopulated() {
        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .build();
        final SubmissionQuestion orgNameQuestionNoResponse = SubmissionQuestion.builder()
                .response(orgName)
                .questionId("APPLICANT_ORG_NAME")
                .validation(validation)
                .build();

        final SubmissionQuestion orgTypeQuestionNoResponse = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_TYPE")
                .validation(validation)
                .build();

        final SubmissionQuestion orgAddressQuestionNoResponse = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_ADDRESS")
                .validation(validation)
                .build();

        final SubmissionQuestion amountQuestionNoResponse = SubmissionQuestion.builder()
                .questionId("APPLICANT_AMOUNT")
                .validation(validation)
                .build();

        final SubmissionQuestion companiesHouseQuestionNoResponse = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_COMPANIES_HOUSE")
                .validation(validation)
                .build();

        final SubmissionQuestion charitiesCommissionQuestionNoResponse = SubmissionQuestion.builder()
                .questionId("APPLICANT_ORG_CHARITY_NUMBER")
                .validation(validation)
                .build();

        section = SubmissionSection.builder()
                .sectionId("ESSENTIAL")
                .sectionStatus(SubmissionSectionStatus.IN_PROGRESS)
                .questions(List.of(orgNameQuestionNoResponse, orgTypeQuestionNoResponse,
                        orgAddressQuestionNoResponse, amountQuestionNoResponse,
                        companiesHouseQuestionNoResponse,
                        charitiesCommissionQuestionNoResponse, question))
                .build();

        final SubmissionDefinition definition = SubmissionDefinition.builder()
                .sections(List.of(section))
                .build();

        submission.setDefinition(definition);
        submission.getApplicant().setOrganisationProfile(null);
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

        Submission methodResponse = serviceUnderTest.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        verify(submissionRepository).findById(SUBMISSION_ID);
        assertEquals(methodResponse, submission);
    }

    @Test
    void getSubmissionsByApplicantId_SubmissionNotFound() {
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class,
                () -> serviceUnderTest.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID));
        verify(submissionRepository).findById(SUBMISSION_ID);
        assertTrue(result.getMessage()
                .contains(String.format("No Submission with ID %s was found", SUBMISSION_ID)));
    }

    @Test
    void getSectionBySectionId_ReturnsExpectedResult() {
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

        SubmissionSection methodResponse = serviceUnderTest.getSectionBySectionId(SUBMISSION_ID, SECTION_ID_1);

        verify(submissionRepository).findById(SUBMISSION_ID);
        assertEquals(methodResponse, section);
    }

    @Test
    void getSectionBySectionId__SubmissionNotFound() {
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class,
                () -> serviceUnderTest.getSectionBySectionId(SUBMISSION_ID, SECTION_ID_1));
        verify(submissionRepository).findById(SUBMISSION_ID);
        assertTrue(result.getMessage()
                .contains(String.format("No Submission with ID %s was found", SUBMISSION_ID)));
    }

    @Test
    void getSectionBySectionId__SectionNotFound() {
        final String NO_SECTION_ID = "NONE";
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

        Exception result = assertThrows(NotFoundException.class,
                () -> serviceUnderTest.getSectionBySectionId(SUBMISSION_ID, NO_SECTION_ID));
        verify(submissionRepository).findById(SUBMISSION_ID);
        assertTrue(result.getMessage()
                .contains(String.format("No Section with ID %s was found", NO_SECTION_ID)));
    }

    @ParameterizedTest
    @MethodSource("provideSectionParams")
    void getNextNavigation_ReturnsExpectedNavigationParams(final UUID submissionId, final String sectionId,
                                                           final String questionId, final boolean saveAndExit,
                                                           final Map<String, Object> expectedNextNavigation) {
        SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId("1")
                .fieldTitle("Are you eligable?")
                .build();

        SubmissionQuestion question2 = SubmissionQuestion.builder()
                .questionId("2")
                .fieldTitle("How much money do you want?")
                .build();

        SubmissionQuestion question3 = SubmissionQuestion.builder()
                .questionId("3")
                .fieldTitle("Have you read the Ts & Cs??")
                .build();

        SubmissionSection section = SubmissionSection.builder()
                .sectionId("4")
                .questions(List.of(question, question2, question3))
                .sectionTitle("Eligiblilty")
                .build();

        Submission submission = Submission.builder()
                .definition(
                        SubmissionDefinition.builder()
                                .sections(List.of(section))
                                .build())
                .id(submissionId)
                .build();

        when(submissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));

        final GetNavigationParamsDto expected = GetNavigationParamsDto.builder()
                .responseAccepted(Boolean.TRUE)
                .nextNavigation(expectedNextNavigation)
                .build();

        GetNavigationParamsDto methodResponse = serviceUnderTest.getNextNavigation(submissionId, sectionId,
                questionId, saveAndExit);
        assertThat(methodResponse).isEqualTo(expected);
    }

    @Test
    void getNextNavigation_ThrowsNotFoundException_IfSectionIdNotFound() {

        final UUID submissionId = UUID.randomUUID();
        final String sectionId = "section_1234";
        final String questionId = "question_1234";

        SubmissionDefinition definition = SubmissionDefinition.builder()
                .sections(Collections.emptyList())
                .build();

        Submission submission = Submission.builder()
                .definition(definition)
                .id(submissionId)
                .build();

        when(submissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));

        assertThrows(NotFoundException.class,
                () -> serviceUnderTest.getNextNavigation(submissionId, sectionId, questionId, false));
    }

    @Test
    void getQuestionByQuestionId_returnsExpectedQuestion() {
        when(submissionRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

        SubmissionQuestion methodResponse = serviceUnderTest.getQuestionByQuestionId(SUBMISSION_ID,
                QUESTION_ID);

        verify(submissionRepository).findById(SUBMISSION_ID);
        assertEquals(methodResponse, question);
    }

    @Test
    void saveQuestionResponse_SavesResponse() {

        final CreateQuestionResponseDto organisationNameResponse = CreateQuestionResponseDto.builder()
                .questionId(QUESTION_ID)
                .submissionId(SUBMISSION_ID)
                .response("AND Digital")
                .build();

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        final ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);

        serviceUnderTest.saveQuestionResponse(organisationNameResponse, SUBMISSION_ID, SECTION_ID_1);

        verify(submissionRepository).save(submissionCaptor.capture());

        // Bit of a long shortcut but rather than re-declaring a stubbed submission
        // object and all it's children, just capture the one we've edited and test
        // against the new value
        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .flatMap(section -> section.getQuestions().stream())
                .filter(q -> q.getQuestionId().equals(QUESTION_ID))
                .map(SubmissionQuestion::getResponse)
                .findAny()
                .ifPresentOrElse(
                        capturedQuestionResponse -> assertThat(capturedQuestionResponse).isEqualTo(organisationNameResponse.getResponse()),
                        () -> fail(String.format("No response worth value '%s' found", organisationNameResponse.getResponse()))
                );

        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_1))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.IN_PROGRESS),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_1))
                );
    }

    @Test
    void saveEligibilityResponseToYesAltersStatus_SavesResponse() {

        final CreateQuestionResponseDto questionResponse = CreateQuestionResponseDto.builder()
                .questionId("ELIGIBILITY")
                .submissionId(SUBMISSION_ID)
                .response("Yes")
                .build();

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        final ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);


        serviceUnderTest.saveQuestionResponse(questionResponse, SUBMISSION_ID, "ELIGIBILITY");


        verify(submissionRepository).save(submissionCaptor.capture());

        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_SECTION_CANNOT_START_YET))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.NOT_STARTED),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_SECTION_CANNOT_START_YET))
                );
        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_NOT_STARTED))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.NOT_STARTED),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_NOT_STARTED))
                );
        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_1))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.IN_PROGRESS),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_1))
                );
    }

    @Test
    void saveEligibilityResponseToNoAltersStatus_SavesResponse() {
//        Test checks that if Eligibility is Yes then Not Started is changed to Cannot Start Yet
        final CreateQuestionResponseDto questionResponse = CreateQuestionResponseDto.builder()
                .questionId("ELIGIBILITY")
                .submissionId(SUBMISSION_ID)
                .response("No")
                .build();

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        final ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);

        serviceUnderTest.saveQuestionResponse(questionResponse, SUBMISSION_ID, "ELIGIBILITY");

        verify(submissionRepository).save(submissionCaptor.capture());

        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_SECTION_CANNOT_START_YET))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.CANNOT_START_YET),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_SECTION_CANNOT_START_YET))
                );

        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_NOT_STARTED))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.CANNOT_START_YET),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_NOT_STARTED))
                );

        submissionCaptor.getValue()
                .getDefinition()
                .getSections()
                .stream()
                .filter(section -> section.getSectionId().equals(SECTION_ID_1))
                .findFirst()
                .ifPresentOrElse(
                        capturedSectionResponse -> assertThat(capturedSectionResponse.getSectionStatus()).isEqualTo(SubmissionSectionStatus.IN_PROGRESS),
                        () -> fail(String.format("No section with ID '%s' found", SECTION_ID_1))
                );
    }

    @Test
    void saveQuestionResponse_ThrowsNotFoundException_IfQuestionNotFound() {

        final CreateQuestionResponseDto organisationNameResponse = CreateQuestionResponseDto.builder()
                .questionId("AN-INVALID-QUESTION-ID")
                .submissionId(SUBMISSION_ID)
                .response("AND Digital")
                .build();

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        Throwable exception = assertThrows(NotFoundException.class, () -> serviceUnderTest.saveQuestionResponse(organisationNameResponse, SUBMISSION_ID, SECTION_ID_1));
        assertEquals("No question with ID AN-INVALID-QUESTION-ID was found", exception.getMessage());
    }

    @Test
    void saveQuestionResponse_ThrowsNotFoundException_IfSectionNotFound() {

        final CreateQuestionResponseDto organisationNameResponse = CreateQuestionResponseDto.builder()
                .questionId("AN-INVALID-QUESTION-ID")
                .submissionId(SUBMISSION_ID)
                .response("AND Digital")
                .build();

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);

        Throwable exception = assertThrows(NotFoundException.class, () -> serviceUnderTest.saveQuestionResponse(organisationNameResponse, SUBMISSION_ID, "invalid_section_id"));
        assertEquals("No section with ID invalid_section_id was found", exception.getMessage());
    }

    @Test
    void isSubmissionReadyToBeSubmitted_returnsFalse_WhenGrantApplicationNotPublished() {
        GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationStatus(GrantApplicantStatus.DRAFT).build();
        submission.setApplication(grantApplication);

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        Boolean isReadyToBeSubmitted = serviceUnderTest.isSubmissionReadyToBeSubmitted(submission.getId());
        assertThat(isReadyToBeSubmitted).isFalse();
    }

    @Test
    void isSubmissionReadyToBeSubmitted_returnTrueWhenAllMandatoryQuestionsHaveBeenAnswered__responseCase() {
        GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationStatus(GrantApplicantStatus.PUBLISHED).build();
        SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder().mandatory(true)
                .build();
        question.setValidation(validation);
        question.setResponse("test");
        submission.setApplication(grantApplication);

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        Boolean isReadyToBeSubmitted = serviceUnderTest.isSubmissionReadyToBeSubmitted(submission.getId());
        assertThat(isReadyToBeSubmitted).isTrue();
    }

    @Test
    void isSubmissionReadyToBeSubmitted_returnTrueWhenAllMandatoryQuestionsHaveBeenAnswered__multiResponseCase() {
        GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationStatus(GrantApplicantStatus.PUBLISHED).build();
        SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder().mandatory(true)
                .build();
        question.setValidation(validation);
        String[] strings = new String[1];
        strings[0] = "test";
        question.setMultiResponse(strings);
        submission.setApplication(grantApplication);

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        Boolean isReadyToBeSubmitted = serviceUnderTest.isSubmissionReadyToBeSubmitted(submission.getId());
        assertThat(isReadyToBeSubmitted).isTrue();
    }

    @Test
    void isSubmissionReadyToBeSubmitted_returnFalseWhenAllMandatoryQuestionsHaveNotBeenAnswered() {
        GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationStatus(GrantApplicantStatus.PUBLISHED).build();
        SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder().mandatory(true)
                .build();
        question.setValidation(validation);
        submission.setApplication(grantApplication);

        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        Boolean isReadyToBeSubmitted = serviceUnderTest.isSubmissionReadyToBeSubmitted(submission.getId());
        assertThat(isReadyToBeSubmitted).isFalse();
    }

    @Test
    void submit_ThrowsSubmissionNotReadyException_IfSubmissionCannotBeSubmitted() {
        final String emailAddress = "test@email.com";
        doReturn(false).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);
        assertThrows(SubmissionNotReadyException.class, () -> serviceUnderTest.submit(submission, emailAddress));
    }

    @Test
    void submit_ThrowsSubmissionAlreadySubmittedException_IfSubmissionAlreadySubmitted() {
        final String emailAddress = "test@email.com";
        final Submission alreadySubmittedSubmission = Submission.builder()
                .status(SubmissionStatus.SUBMITTED)
                .id(SUBMISSION_ID)
                .build();

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        assertThrows(SubmissionAlreadySubmittedException.class,
                () -> serviceUnderTest.submit(alreadySubmittedSubmission, emailAddress));
    }

    @Test
    void submit_SubmitsTheApplicationForm() {
        final String emailAddress = "test@email.com";
        final ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        serviceUnderTest.submit(submission, emailAddress);

        verify(notifyClient).sendConfirmationEmail(emailAddress, submission);
        verify(submissionRepository).save(submissionCaptor.capture());

        final Submission capturedSubmission = submissionCaptor.getValue();

        assertThat(capturedSubmission.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(capturedSubmission.getSubmittedDate()).isEqualTo(ZonedDateTime.now(clock));
        capturedSubmission.getDefinition()
                .getSections()
                .forEach(s -> assertThat(s.getSectionStatus())
                        .isEqualTo(SubmissionSectionStatus.COMPLETED));
    }

    @Test
    void submit_CreatesDiligenceCheck() {

        final String emailAddress = "test@email.com";
        final ArgumentCaptor<DiligenceCheck> diligenceCheckCaptor = ArgumentCaptor
                .forClass(DiligenceCheck.class);

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        serviceUnderTest.submit(submission, emailAddress);
        verify(diligenceCheckRepository).save(diligenceCheckCaptor.capture());

        final DiligenceCheck capturedCheck = diligenceCheckCaptor.getValue();

        assertThat(capturedCheck.getOrganisationName()).isEqualTo(orgName);
        assertThat(capturedCheck.getAddressStreet()).isEqualTo(addressResponse[0]);
        assertThat(capturedCheck.getAddressTown()).isEqualTo(addressResponse[2]);
        assertThat(capturedCheck.getAddressCounty()).isEqualTo(addressResponse[3]);
        assertThat(capturedCheck.getAddressPostcode()).isEqualTo(addressResponse[4]);
        assertThat(capturedCheck.getApplicationAmount()).isEqualTo(amount);
        assertThat(capturedCheck.getCompaniesHouseNumber()).isEqualTo(companiesHouseNo);
        assertThat(capturedCheck.getCharityNumber()).isEqualTo(charityNo);
        assertThat(capturedCheck.getApplicationNumber()).isEqualTo(submission.getGapId());
    }

    @Test
    void submit_ThrowsIllegalArgumentException_IfSectionNotPresentInSubmission() {
        final String emailAddress = "test@email.com";
        final SubmissionDefinition definitionWithNoEssentialSection = SubmissionDefinition.builder()
                .sections(Collections.emptyList())
                .build();
        final Submission submissionWithoutEssentialSection = Submission.builder()
                .definition(definitionWithNoEssentialSection)
                .status(SubmissionStatus.IN_PROGRESS)
                .id(SUBMISSION_ID)
                .build();

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        assertThrows(IllegalArgumentException.class,
                () -> serviceUnderTest.submit(submissionWithoutEssentialSection, emailAddress));
    }

    @ParameterizedTest
    @MethodSource("provideSectionsWithMissingQuestions")
    void submit_ThrowsIllegalArgumentException_IfQuestionNotPresentInSection(List<SubmissionQuestion> questions) {
        final String emailAddress = "test@email.com";
        final SubmissionSection essentialSection = SubmissionSection.builder()
                .sectionId("ESSENTIAL")
                .questions(questions)
                .build();
        final SubmissionDefinition definitionWithNoEssentialSection = SubmissionDefinition.builder()
                .sections(List.of(essentialSection))
                .build();
        final Submission submissionWithoutEssentialSection = Submission.builder()
                .definition(definitionWithNoEssentialSection)
                .id(SUBMISSION_ID)
                .status(SubmissionStatus.IN_PROGRESS)
                .build();

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        assertThrows(IllegalArgumentException.class,
                () -> serviceUnderTest.submit(submissionWithoutEssentialSection, emailAddress));
    }

    @Test
    void submit_CreatesGrantBeneficiary() {

        final String emailAddress = "test@email.com";
        final ArgumentCaptor<GrantBeneficiary> grantBeneficiaryCaptor = ArgumentCaptor
                .forClass(GrantBeneficiary.class);

        doReturn(true).when(serviceUnderTest).isSubmissionReadyToBeSubmitted(SUBMISSION_ID);

        serviceUnderTest.submit(submission, emailAddress);

        verify(grantBeneficiaryRepository).save(grantBeneficiaryCaptor.capture());

        final GrantBeneficiary capturedBeneficiary = grantBeneficiaryCaptor.getValue();

        assertThat(capturedBeneficiary.getSubmissionId())
                .isEqualTo(submission.getId());
        assertThat(capturedBeneficiary.getSchemeId())
                .isEqualTo(submission.getApplication().getGrantScheme().getId());
        assertThat(capturedBeneficiary.getApplicationId()).isEqualTo(submission.getApplication().getId());
        assertThat(capturedBeneficiary.getCreatedBy()).isEqualTo(submission.getApplicant().getId());
        assertThat(capturedBeneficiary.getLocationSco()).isTrue();
        assertThat(capturedBeneficiary.getLocationSwEng()).isTrue();
        assertThat(capturedBeneficiary.getLocationMidEng()).isTrue();
        assertThat(capturedBeneficiary.getLocationSeEng()).isFalse();
        assertThat(capturedBeneficiary.getLocationNwEng()).isFalse();
        assertThat(capturedBeneficiary.getGapId()).isEqualTo(submission.getGapId());
    }

    @Test
    void createSubmissionFromApplication__returnCreateSubmissionResponseDto() throws JsonProcessingException {
        final SubmissionDefinition definition = new SubmissionDefinition();
        final GrantApplicant grantApplicant = new GrantApplicant();
        final GrantScheme grantScheme = new GrantScheme();
        final ApplicationDefinition applicationDefinition = new ApplicationDefinition();
        final GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationName("Test Application")
                .definition(applicationDefinition)
                .grantScheme(grantScheme)
                .version(1)
                .build();
        final LocalDateTime now = LocalDateTime.now(clock);
        final Submission submission = Submission.builder()
                .scheme(grantScheme)
                .application(grantApplication)
                .applicant(grantApplicant)
                .version(grantApplication.getVersion())
                .created(now)
                .createdBy(grantApplicant)
                .lastUpdated(now)
                .lastUpdatedBy(grantApplicant)
                .applicationName(grantApplication.getApplicationName())
                .status(SubmissionStatus.IN_PROGRESS)
                .definition(definition)
                .build();

        when(submissionRepository.save(submission)).thenReturn(submission);
        CreateSubmissionResponseDto response = serviceUnderTest.createSubmissionFromApplication(grantApplicant,
                grantApplication);
        CreateSubmissionResponseDto expected = CreateSubmissionResponseDto.builder()
                .submissionCreated(true)
                .submissionId(submission.getId())
                .build();

        assertEquals(expected, response);
    }


    @Test
    void doesSubmissionExist_ReturnsTrue_IfSubmissionExists() {
        List<Submission> submissions = new ArrayList<>();
        submissions.add(submission);
        when(submissionRepository.findByApplicantId(1)).thenReturn(submissions);
        boolean result = serviceUnderTest.doesSubmissionExist(submission.getApplicant(), grantApplication);
        assertTrue(result);
    }

    @Test
    void doesSubmissionExist_ReturnsFalse_IfSubmissionDoesNotExists() {
        when(submissionRepository.findByApplicantId(1)).thenReturn(new ArrayList<>());
        boolean result = serviceUnderTest.doesSubmissionExist(submission.getApplicant(), grantApplication);
        assertFalse(result);
    }

    @Test
    void handleSectionReview_returnSectionStatusInProgress() {
        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        when(submissionRepository.save(submission)).thenReturn(submission);
        final SubmissionSectionStatus response = serviceUnderTest.handleSectionReview(SUBMISSION_ID, SECTION_ID_1, false);
        assertEquals(SubmissionSectionStatus.IN_PROGRESS, response);
    }

    @Test
    void handleSectionReview_returnSectionStatusCompleted() {
        doReturn(submission)
                .when(serviceUnderTest).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        when(submissionRepository.save(submission)).thenReturn(submission);
        final SubmissionSectionStatus response = serviceUnderTest.handleSectionReview(SUBMISSION_ID, SECTION_ID_1, true);
        assertEquals(SubmissionSectionStatus.COMPLETED, response);
    }
}