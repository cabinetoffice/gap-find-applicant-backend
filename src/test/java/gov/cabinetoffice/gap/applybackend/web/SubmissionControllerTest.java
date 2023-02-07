package gov.cabinetoffice.gap.applybackend.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cabinetoffice.gap.applybackend.constants.APIConstants;
import gov.cabinetoffice.gap.applybackend.dto.api.*;
import gov.cabinetoffice.gap.applybackend.enums.GrantAttachmentStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus;
import gov.cabinetoffice.gap.applybackend.exception.AttachmentException;
import gov.cabinetoffice.gap.applybackend.exception.GrantApplicationNotPublishedException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.exception.SubmissionAlreadyCreatedException;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {
    final LocalDateTime timestamp = LocalDateTime.now();
    final Instant instant = Instant.now();
    final String QUESTION_ID_1 = "APPLICANT_ORG_NAME";
    final String QUESTION_ID_2 = "APPLICANT_ORG_ADDRESS";
    final String QUESTION_ID_3 = "APPLICANT_ORG_COMPANIES_HOUSE";
    final String QUESTION_ID_4 = "CUSTOM_INPUT_1";
    final String SECTION_ID_1 = "ELIGIBILITY";
    final String SECTION_ID_2 = "CUSTOM_SECTION_1";
    final String SECTION_TITLE_1 = "Eligibility";
    final String SECTION_TITLE_2 = "Project Status";
    final GrantScheme scheme = GrantScheme.builder()
            .id(1)
            .version(1)
            .funderId(1)
            .lastUpdated(instant)
            .email("test@and.digital")
            .name("Test Scheme")
            .ggisIdentifier("Test GGIS Identifier")
            .build();
    final GrantApplication application = GrantApplication.builder()
            .id(1)
            .applicationName("Test Application")
            .created(instant)
            .lastUpdated(instant)
            .definition(new ApplicationDefinition())
            .grantScheme(scheme)
            .version(1)
            .lastUpdateBy(1)
            .build();
    final SubmissionQuestion question1 = SubmissionQuestion.builder()
            .questionId(QUESTION_ID_1)
            .profileField("ORG_NAME")
            .fieldTitle("Enter the name of your organisation")
            .hintText("This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission")
            .responseType(SubmissionQuestionResponseType.ShortAnswer)
            .validation(
                    SubmissionQuestionValidation.builder()
                            .mandatory(true)
                            .minLength(2)
                            .maxLength(250)
                            .build())
            .build();
    final SubmissionQuestion question2 = SubmissionQuestion.builder()
            .questionId(QUESTION_ID_2)
            .profileField("ORG_ADDRESS")
            .fieldTitle("Enter your organisation's address")
            .responseType(SubmissionQuestionResponseType.AddressInput)
            .validation(
                    SubmissionQuestionValidation.builder()
                            .mandatory(true)
                            .build())
            .build();
    final SubmissionQuestion question3 = SubmissionQuestion.builder()
            .questionId(QUESTION_ID_3)
            .profileField("ORG_COMPANIES_HOUSE")
            .fieldTitle("Does your organisation have a Companies House number?")
            .hintText("Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.")
            .responseType(SubmissionQuestionResponseType.YesNoPlusValue)
            .validation(
                    SubmissionQuestionValidation.builder()
                            .minLength(5)
                            .maxLength(100)
                            .build())
            .build();
    final SubmissionSection section1 = SubmissionSection.builder()
            .sectionId(SECTION_ID_1)
            .sectionTitle(SECTION_TITLE_1)
            .sectionStatus(SubmissionSectionStatus.IN_PROGRESS)
            .questions(List.of(question1, question2, question3))
            .build();
    final SubmissionQuestion question4 = SubmissionQuestion.builder()
            .questionId(QUESTION_ID_4)
            .fieldTitle("Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees")
            .hintText("Optional additional helptext")
            .responseType(SubmissionQuestionResponseType.LongAnswer)
            .validation(
                    SubmissionQuestionValidation.builder()
                            .mandatory(true)
                            .minLength(100)
                            .maxLength(2000)
                            .minWords(50)
                            .maxWords(400)
                            .build())
            .build();
    final SubmissionSection section2 = SubmissionSection.builder()
            .sectionId(SECTION_ID_2)
            .sectionTitle(SECTION_TITLE_2)
            .sectionStatus(SubmissionSectionStatus.IN_PROGRESS)
            .questions(List.of(question4))
            .build();
    final SubmissionDefinition definition = SubmissionDefinition.builder()
            .sections(List.of(section1, section2))
            .build();
    final GetSectionDto getSectionDto1 = GetSectionDto.builder()
            .sectionId(SECTION_ID_1)
            .sectionTitle(SECTION_TITLE_1)
            .sectionStatus(SubmissionSectionStatus.IN_PROGRESS.toString())
            .questionIds(List.of(QUESTION_ID_1, QUESTION_ID_2, QUESTION_ID_3))
            .build();
    final GetSectionDto getSectionDto2 = GetSectionDto.builder()
            .sectionId(SECTION_ID_2)
            .sectionTitle(SECTION_TITLE_2)
            .sectionStatus(SubmissionSectionStatus.IN_PROGRESS.toString())
            .questionIds(List.of(QUESTION_ID_4))
            .build();
    private final long APPLICANT_ID = 1;
    private final long PROFILE_ID = 1;
    final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
            .id(PROFILE_ID)
            .legalName("AND Digital")
            .charityCommissionNumber("45")
            .companiesHouseNumber("000010")
            .addressLine1("AND Digital")
            .addressLine2("9 George Square")
            .town("Glasgow")
            .postcode("G2 1QQ")
            .county("Renfrewshire")
            .build();
    private final UUID APPLICANT_USER_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
    final GrantApplicant grantApplicant = GrantApplicant.builder()
            .id(APPLICANT_ID)
            .userId(APPLICANT_USER_ID)
            .organisationProfile(grantApplicantOrganisationProfile)
            .build();
    private final UUID SUBMISSION_ID = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");
    final Submission submission = Submission
            .builder()
            .id(SUBMISSION_ID)
            .applicant(grantApplicant)
            .scheme(scheme)
            .application(application)
            .version(1)
            .created(timestamp)
            .createdBy(grantApplicant)
            .lastUpdated(timestamp)
            .lastUpdatedBy(grantApplicant)
            .applicationName("Test Submission")
            .status(SubmissionStatus.IN_PROGRESS)
            .definition(definition)
            .build();
    final GetSubmissionDto getSubmissionDto1 = GetSubmissionDto.builder()
            .grantSchemeId(1)
            .grantApplicationId(1)
            .grantSubmissionId(SUBMISSION_ID)
            .applicationName("Test Submission")
            .sections(List.of(getSectionDto1, getSectionDto2))
            .submissionStatus(SubmissionStatus.IN_PROGRESS)
            .build();
    final List<GetSubmissionDto> getSubmissionDtos = List.of(getSubmissionDto1);
    final GetQuestionDto getQuestionDto1 = GetQuestionDto.builder()
            .grantSchemeId(1)
            .grantApplicationId(1)
            .grantSubmissionId(SUBMISSION_ID)
            .sectionId(SECTION_ID_1)
            .sectionTitle(SECTION_TITLE_1)
            .question(question1)
            .nextNavigation(GetQuestionNavigationDto.builder()
                    .questionId(QUESTION_ID_2)
                    .sectionId(SECTION_ID_1)
                    .build())
            .build();
    final GetQuestionDto getQuestionDto2 = GetQuestionDto.builder()
            .grantSchemeId(1)
            .grantApplicationId(1)
            .grantSubmissionId(SUBMISSION_ID)
            .sectionId(SECTION_ID_1)
            .sectionTitle(SECTION_TITLE_1)
            .question(question2)
            .previousNavigation(GetQuestionNavigationDto.builder()
                    .questionId(QUESTION_ID_1)
                    .sectionId(SECTION_ID_1)
                    .build())
            .nextNavigation(GetQuestionNavigationDto.builder()
                    .questionId(QUESTION_ID_3)
                    .sectionId(SECTION_ID_1)
                    .build())
            .build();
    final GetQuestionDto getQuestionDto3 = GetQuestionDto.builder()
            .grantSchemeId(1)
            .grantApplicationId(1)
            .grantSubmissionId(SUBMISSION_ID)
            .sectionId(SECTION_ID_1)
            .sectionTitle(SECTION_TITLE_1)
            .question(question3)
            .previousNavigation(GetQuestionNavigationDto.builder()
                    .questionId(QUESTION_ID_2)
                    .sectionId(SECTION_ID_1)
                    .build())
            .build();
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private SubmissionService submissionService;
    @Mock
    private GrantApplicantService grantApplicantService;
    @Mock
    private GrantApplicationService grantApplicationService;
    @Mock
    private GrantAttachmentService grantAttachmentService;
    @Mock
    private AttachmentService attachmentService;

    final String CHRISTMAS_2022_MIDDAY = "2022-12-25T12:00:00.00z";
    final Clock clock = Clock.fixed(Instant.parse(CHRISTMAS_2022_MIDDAY), ZoneId.of("UTC"));

    private SubmissionController controllerUnderTest;

    @BeforeEach
    void setup() {
        controllerUnderTest = new SubmissionController(submissionService, grantApplicantService, grantAttachmentService, grantApplicationService, attachmentService, clock);
    }

    @Test
    void getSubmissions_ReturnsExpectedResponse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID.toString()).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

        grantApplicant.setSubmissions(List.of(submission));
        when(grantApplicantService.getApplicantById(APPLICANT_USER_ID))
                .thenReturn(grantApplicant);

        ResponseEntity<List<GetSubmissionDto>> response = controllerUnderTest.getSubmissions();

        verify(grantApplicantService).getApplicantById(APPLICANT_USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), getSubmissionDtos);
    }

    @Test
    void getSubmission_ReturnsExpectedResponse() {
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<GetSubmissionDto> response = controllerUnderTest.getSubmission(SUBMISSION_ID);

        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), getSubmissionDto1);
    }

    @Test
    void getSection_ReturnsExpectedResponse() {
        when(submissionService.getSectionBySectionId(SUBMISSION_ID, SECTION_ID_1))
                .thenReturn(section1);

        ResponseEntity<SubmissionSection> response = controllerUnderTest.getSection(SUBMISSION_ID, SECTION_ID_1);

        verify(submissionService).getSectionBySectionId(SUBMISSION_ID, SECTION_ID_1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), section1);
    }

    @Test
    void getQuestion_ReturnsExpectedResponseNoNextNavigation() {
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1);

        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getQuestionDto1, response.getBody());
    }

    @Test
    void getQuestion_ReturnsExpectedResponseNextAndPreviousNavigation() {
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_2);

        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getQuestionDto2, response.getBody());
    }

    @Test
    void getQuestion_ReturnsExpectedResponseNoPreviousNavigation() {
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_3);

        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getQuestionDto3, response.getBody());
    }

    @Test
    void getQuestion_ThrowWhenSectionNotFound() {
        String sectionId = "NONE";
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        Exception result = assertThrows(NotFoundException.class, () -> controllerUnderTest.getQuestion(SUBMISSION_ID, sectionId, QUESTION_ID_1));
        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertTrue(result.getMessage().contains(String.format("No Section with ID %s was found", sectionId)));
    }

    @Test
    void getQuestion_ThrowWhenQuestionNotFound() {
        String questionId = "NONE";
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        Exception result = assertThrows(NotFoundException.class, () -> controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, questionId));
        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        assertTrue(result.getMessage().contains(String.format("No question with ID %s was found", questionId)));
    }

    @Test
    void save_savesQuestionResponseAndReturnsExpectedNextNav() {

        final GetNavigationParamsDto nextNav = GetNavigationParamsDto.builder().build();
        final CreateQuestionResponseDto questionResponse = CreateQuestionResponseDto.builder()
                .submissionId(SUBMISSION_ID)
                .questionId(QUESTION_ID_1)
                .build();

        doReturn(nextNav)
                .when(submissionService).getNextNavigation(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false);

        ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.save(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, questionResponse);

        verify(submissionService).saveQuestionResponse(questionResponse, SUBMISSION_ID, SECTION_ID_1);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(nextNav);
    }

    @Test
    void isSubmissionReadyToBeSubmitted_ReturnsExpectedResponse_ReturnTrue() {
        when(submissionService.isSubmissionReadyToBeSubmitted(SUBMISSION_ID))
                .thenReturn(true);

        ResponseEntity<Boolean> response = controllerUnderTest.isSubmissionReadyToBeSubmitted(SUBMISSION_ID);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void isSubmissionReadyToBeSubmitted_ReturnsExpectedResponse_ReturnFalse() {
        when(submissionService.isSubmissionReadyToBeSubmitted(SUBMISSION_ID))
                .thenReturn(false);

        ResponseEntity<Boolean> response = controllerUnderTest.isSubmissionReadyToBeSubmitted(SUBMISSION_ID);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }

    @Test
    void submitApplication_isSuccessfulAndReturnsExpectedResponse() {

        final String emailAddress = "test@email.com";
        final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                .submissionId(SUBMISSION_ID)
                .build();
        final JwtPayload jwtPayload = JwtPayload.builder().email(emailAddress).build();

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<String> response = controllerUnderTest.submitApplication(submitApplication);

        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
        verify(submissionService).submit(submission, emailAddress);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Submitted");
    }

    @Test
    void submitApplication_ThrowsNotFoundException_IfSubmissionNotFound() {

        final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                .submissionId(SUBMISSION_ID)
                .build();

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> controllerUnderTest.submitApplication(submitApplication));
        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID);
    }

    @Test
    void createApplication__submissionNotPublished_ThrowException() {
        when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(false);
        GrantApplicationNotPublishedException result = assertThrows(GrantApplicationNotPublishedException.class, () -> controllerUnderTest.createApplication(1));

        assertTrue(result.getMessage().contains(String.format("Grant Application %s is not been published yet.", 1)));
    }

    @Test
    void createApplication__submissionAlreadyExists_ThrowSubmissionAlreadyCreatedException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID.toString()).build();

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(true);
        when(grantApplicationService.getGrantApplicationById(1)).thenReturn(application);
        when(grantApplicantService.getApplicantById(grantApplicant.getUserId())).thenReturn(grantApplicant);
        when(submissionService.doesSubmissionExist(grantApplicant, application)).thenReturn(true);

        SubmissionAlreadyCreatedException result = assertThrows(SubmissionAlreadyCreatedException.class, () -> controllerUnderTest.createApplication(1));

        assertTrue(result.getMessage().contains("SUBMISSION_EXISTS"));
    }

    @Test
    void createApplication() throws JsonProcessingException {
        final UUID submissionId = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");
        final SubmissionDefinition definition = new SubmissionDefinition();

        final ApplicationDefinition applicationDefinition = new ApplicationDefinition();
        final GrantScheme grantScheme = new GrantScheme();
        final GrantApplication grantApplication = GrantApplication.builder().id(1)
                .applicationName("Test Application")
                .definition(applicationDefinition)
                .grantScheme(grantScheme)
                .version(1)
                .build();
        final CreateSubmissionResponseDto createSubmissionResponseDto = CreateSubmissionResponseDto.builder()
                .submissionCreated(true)
                .submissionId(submissionId)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID.toString()).build();
        final UUID applicantId = UUID.fromString(jwtPayload.getSub());
        final GrantApplicant grantApplicant = GrantApplicant.builder().userId(applicantId).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

        when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(true);
        when(grantApplicationService.getGrantApplicationById(1)).thenReturn(grantApplication);
        when(grantApplicantService.getApplicantById(applicantId)).thenReturn(grantApplicant);
        when(submissionService.createSubmissionFromApplication(grantApplicant, grantApplication)).thenReturn(createSubmissionResponseDto);

        ResponseEntity<CreateSubmissionResponseDto> response = controllerUnderTest.createApplication(1);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createSubmissionResponseDto, response.getBody());
    }

    @Test
    void postSectionReview_completedSectionStatus() {
        final SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().isComplete(true).build();
        when(submissionService.handleSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto.getIsComplete())).thenReturn(SubmissionSectionStatus.COMPLETED);
        final ResponseEntity<String> result = controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(), String.format("Section with ID %s status has been updated to %s.", SECTION_ID_1, SubmissionSectionStatus.COMPLETED));
    }

    @Test
    void postSectionReview_inProgressSectionStatus() {
        final SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().isComplete(false).build();
        when(submissionService.handleSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto.getIsComplete())).thenReturn(SubmissionSectionStatus.IN_PROGRESS);
        final ResponseEntity<String> result = controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(), String.format("Section with ID %s status has been updated to %s.", SECTION_ID_1, SubmissionSectionStatus.IN_PROGRESS));
    }

    @Test
    void postSectionReview_bodyIsNull() {
        SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().build();
        assertThrows(NullPointerException.class, () -> controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto));
    }
    @Test
    void isSubmissionSubmitted_returnsExpectedResult() {
        when(submissionService.hasSubmissionBeenSubmitted(SUBMISSION_ID))
                .thenReturn(true);

        final ResponseEntity<Boolean> methodResponse = controllerUnderTest.isSubmissionSubmitted(SUBMISSION_ID);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isTrue();
    }

    private static Stream<Arguments> provideGrantAttachmentUpdates() {
        return Stream.of(
                Arguments.of(
                        UpdateAttachmentDto.builder()
                                .isClean(true)
                                .uri("https://uri.com")
                                .build(),
                        GrantAttachmentStatus.AVAILABLE
                ),
                Arguments.of(
                        UpdateAttachmentDto.builder()
                                .isClean(false)
                                .uri("https://uri.com")
                                .build(),
                        GrantAttachmentStatus.QUARANTINED
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideGrantAttachmentUpdates")
    void updateAttachment_UpdatesExpectedAttachment(UpdateAttachmentDto update, GrantAttachmentStatus status) {

        final GrantAttachment attachment = GrantAttachment.builder().build();

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        when(grantAttachmentService.getAttachmentBySubmissionAndQuestion(submission, QUESTION_ID_1))
                .thenReturn(attachment);

        final ArgumentCaptor<GrantAttachment> attachmentCaptor = ArgumentCaptor.forClass(GrantAttachment.class);

        final ResponseEntity<String> methodResponse = controllerUnderTest.updateAttachment(SUBMISSION_ID, QUESTION_ID_1, update);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("Attachment Updated");

        verify(grantAttachmentService).save(attachmentCaptor.capture());
        assertThat(attachmentCaptor.getValue().getStatus()).isEqualTo(status);
        assertThat(attachmentCaptor.getValue().getLastUpdated()).isEqualTo(Instant.now(clock));
        assertThat(attachmentCaptor.getValue().getLocation()).isEqualTo(update.getUri());
    }

    // it's frightening how simultaneously good and bad this test is
    @Test
    void postAttachment_SavesTheDocumentAndCreatesADatabaseEntry() {

        final String questionId = UUID.randomUUID().toString();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .allowedTypes(new String[] {"txt"})
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId(questionId)
                .validation(validation)
                .build();

        section2.setQuestions(List.of(question));

        final MultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        final GetNavigationParamsDto expectedNavigation = GetNavigationParamsDto.builder().build();

        when(grantApplicantService.getApplicantFromPrincipal())
                .thenReturn(grantApplicant);

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        when(submissionService.getNextNavigation(SUBMISSION_ID, SECTION_ID_2, questionId, false))
                .thenReturn(expectedNavigation);

        final ArgumentCaptor<GrantAttachment> attachmentCaptor = ArgumentCaptor.forClass(GrantAttachment.class);

        final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file);

        verify(attachmentService).attachmentFile(application.getId() + "/" + SUBMISSION_ID + "/" + questionId + "/" + file.getOriginalFilename(), file);
        verify(grantAttachmentService).createAttachment(attachmentCaptor.capture());
        verify(submissionService).saveSubmission(submission);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(expectedNavigation);
    }

    @Test
    void postAttachment_ThrowsAttachmentException_IfAttachmentIsNull() {

        final String questionId = UUID.randomUUID().toString();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .allowedTypes(new String[] {"txt"})
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId(questionId)
                .validation(validation)
                .build();

        section2.setQuestions(List.of(question));

        when(grantApplicantService.getApplicantFromPrincipal())
                .thenReturn(grantApplicant);

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, null));

        assertThat(ex.getMessage()).isEqualTo("Select a file to continue");
    }

    @Test
    void postAttachment_ThrowsAttachmentException_IfAttachmentIsEmpty() {

        final String questionId = UUID.randomUUID().toString();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .allowedTypes(new String[] {"txt"})
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId(questionId)
                .validation(validation)
                .build();

        section2.setQuestions(List.of(question));

        final MultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        when(grantApplicantService.getApplicantFromPrincipal())
                .thenReturn(grantApplicant);

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file));

        assertThat(ex.getMessage()).isEqualTo("The selected file is empty");
    }

    @Test
    void postAttachment_ThrowsAttachmentException_IfQuestionAlreadyHasAnAttachment() {

        final String questionId = UUID.randomUUID().toString();

        final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                .mandatory(true)
                .allowedTypes(new String[] {"txt"})
                .build();

        final SubmissionQuestion question = SubmissionQuestion.builder()
                .questionId(questionId)
                .validation(validation)
                .attachmentId(UUID.randomUUID())
                .build();

        section2.setQuestions(List.of(question));

        final MultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        when(grantApplicantService.getApplicantFromPrincipal())
                .thenReturn(grantApplicant);

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file));

        assertThat(ex.getMessage()).isEqualTo("You can only select up to 1 file at the same time");
    }

    @Test
    void removeAttachment_RemovesFileFromS3_AndDeletesDatabaseEntry() {

        final int applicationId = submission.getApplication().getId();
        final UUID attachmentId = UUID.randomUUID();
        final GrantAttachment attachment = GrantAttachment.builder()
                .id(attachmentId)
                .build();

        final GetNavigationParamsDto expectedNav = GetNavigationParamsDto.builder()
                .responseAccepted(Boolean.TRUE)
                .nextNavigation(Map.of(
                        APIConstants.NAVIGATION_SECTION_ID, SECTION_ID_1,
                        APIConstants.NAVIGATION_QUESTION_ID, QUESTION_ID_1
                ))
                .build();

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(SUBMISSION_ID))
                .thenReturn(submission);

        when(grantAttachmentService.getAttachment(attachmentId))
                .thenReturn(attachment);

        final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.removeAttachment(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, attachmentId);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(expectedNav);

        verify(attachmentService).deleteAttachment(attachment, applicationId, SUBMISSION_ID, QUESTION_ID_1);
        verify(submissionService).deleteQuestionResponse(SUBMISSION_ID, QUESTION_ID_1);
    }

    @Test
    void getNextNavigationForQuestion_ReturnsExpectedResult() {

        final GetNavigationParamsDto expectedNav = GetNavigationParamsDto.builder()
                .responseAccepted(Boolean.TRUE)
                .nextNavigation(Map.of(
                        APIConstants.NAVIGATION_SECTION_ID, SECTION_ID_1,
                        APIConstants.NAVIGATION_QUESTION_ID, QUESTION_ID_1
                ))
                .build();

        when(submissionService.getNextNavigation(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false))
                .thenReturn(expectedNav);

        final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.getNextNavigationForQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(expectedNav);
    }
}