package gov.cabinetoffice.gap.applybackend.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cabinetoffice.gap.applybackend.constants.APIConstants;
import gov.cabinetoffice.gap.applybackend.dto.api.CreateQuestionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.CreateSubmissionResponseDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetNavigationParamsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetQuestionNavigationDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetSectionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetSubmissionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.SubmissionReviewBodyDto;
import gov.cabinetoffice.gap.applybackend.dto.api.SubmitApplicationDto;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateAttachmentDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import gov.cabinetoffice.gap.applybackend.enums.GrantAttachmentStatus;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.exception.AttachmentException;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.GrantApplicationNotPublishedException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.exception.UnauthorizedException;
import gov.cabinetoffice.gap.applybackend.model.ApplicationDefinition;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantAttachment;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.model.SubmissionDefinition;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestionValidation;
import gov.cabinetoffice.gap.applybackend.model.SubmissionSection;
import gov.cabinetoffice.gap.applybackend.service.AttachmentService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import gov.cabinetoffice.gap.applybackend.service.GrantAttachmentService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import gov.cabinetoffice.gap.applybackend.service.SecretAuthService;
import gov.cabinetoffice.gap.applybackend.service.SpotlightService;
import gov.cabinetoffice.gap.applybackend.service.SubmissionService;
import gov.cabinetoffice.gap.applybackend.service.ZipService;
import gov.cabinetoffice.gap.applybackend.utils.SecurityContextHelper;
import gov.cabinetoffice.gap.eventservice.service.EventLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus.IN_PROGRESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {
    private static MockedStatic<SecurityContextHelper> mockSecurityContextHelper;
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
            .applicationStatus(GrantApplicationStatus.PUBLISHED)
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
    final String CHRISTMAS_2022_MIDDAY = "2022-12-25T12:00:00.00z";
    final Clock clock = Clock.fixed(Instant.parse(CHRISTMAS_2022_MIDDAY), ZoneId.of("UTC"));
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
    private final String APPLICANT_USER_ID = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
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
            .status(IN_PROGRESS)
            .definition(definition)
            .build();
    final GetSubmissionDto getSubmissionDto1 = GetSubmissionDto.builder()
            .grantSchemeId(1)
            .grantApplicationId(1)
            .grantSubmissionId(SUBMISSION_ID)
            .applicationName("Test Submission")
            .sections(List.of(getSectionDto1, getSectionDto2))
            .submissionStatus(IN_PROGRESS)
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
    private SecretAuthService secretAuthService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private SpotlightService spotlightService;
    @Mock
    private GrantMandatoryQuestionService mandatoryQuestionService;
    @Mock
    private EventLogService eventLogService;
    @Mock
    private ZipService zipService;
    private SubmissionController controllerUnderTest;

    @BeforeEach
    void setup() {
        // TODO holy argument list batman, this class needs broken down!§§§
        controllerUnderTest = new SubmissionController(submissionService, grantApplicantService,
                grantAttachmentService, grantApplicationService, spotlightService, mandatoryQuestionService,
                zipService, secretAuthService, attachmentService, eventLogService, clock);

    }

    @Nested
    class withSecurityContextSet {
        @BeforeEach
        void setup() {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        }

        @Test
        void getSection_ReturnsExpectedResponse() {
            when(submissionService.getSectionBySectionId(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1))
                    .thenReturn(section1);

            ResponseEntity<SubmissionSection> response = controllerUnderTest.getSection(SUBMISSION_ID, SECTION_ID_1);

            verify(submissionService).getSectionBySectionId(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody(), section1);
        }

        @Test
        void isSubmissionSubmitted_returnsExpectedResult() {
            when(submissionService.hasSubmissionBeenSubmitted(APPLICANT_USER_ID, SUBMISSION_ID))
                    .thenReturn(true);

            final ResponseEntity<Boolean> methodResponse = controllerUnderTest.isSubmissionSubmitted(SUBMISSION_ID);

            assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(methodResponse.getBody()).isTrue();
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

            when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                    .thenReturn(submission);

            when(grantAttachmentService.getAttachment(attachmentId))
                    .thenReturn(attachment);

            final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.removeAttachment(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, attachmentId);

            assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(methodResponse.getBody()).isEqualTo(expectedNav);

            verify(attachmentService).deleteAttachment(attachment, applicationId, SUBMISSION_ID, QUESTION_ID_1);
            verify(submissionService).deleteQuestionResponse(APPLICANT_USER_ID, SUBMISSION_ID, QUESTION_ID_1);
            verify(submissionService).handleSectionReview(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, Boolean.FALSE);
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

            when(submissionService.getNextNavigation(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false))
                    .thenReturn(expectedNav);

            final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.getNextNavigationForQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false);

            assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(methodResponse.getBody()).isEqualTo(expectedNav);
        }

        @Nested
        class getSubmissions {
            @Test
            void getSubmissions_ReturnsExpectedResponse() {
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
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                ResponseEntity<GetSubmissionDto> response = controllerUnderTest.getSubmission(SUBMISSION_ID);

                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(response.getBody(), getSubmissionDto1);
            }
        }

        @Nested
        class getQuestion {
            @Test
            void getQuestion_ReturnsExpectedResponseNoNextNavigation() {
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1);

                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(getQuestionDto1, response.getBody());
            }

            @Test
            void getQuestion_ReturnsExpectedResponseNextAndPreviousNavigation() {
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_2);

                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(getQuestionDto2, response.getBody());
            }

            @Test
            void getQuestion_ReturnsExpectedResponseNoPreviousNavigation() {
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                ResponseEntity<GetQuestionDto> response = controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_3);

                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(getQuestionDto3, response.getBody());
            }

            @Test
            void getQuestion_ThrowWhenSectionNotFound() {
                String sectionId = "NONE";
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                Exception result = assertThrows(NotFoundException.class, () -> controllerUnderTest.getQuestion(SUBMISSION_ID, sectionId, QUESTION_ID_1));
                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertTrue(result.getMessage().contains(String.format("No Section with ID %s was found", sectionId)));
            }

            @Test
            void getQuestion_ThrowWhenQuestionNotFound() {
                String questionId = "NONE";
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                Exception result = assertThrows(NotFoundException.class, () -> controllerUnderTest.getQuestion(SUBMISSION_ID, SECTION_ID_1, questionId));
                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                assertTrue(result.getMessage().contains(String.format("No question with ID %s was found", questionId)));
            }
        }

        @Nested
        class save {
            @Test
            void save_savesQuestionResponseAndReturnsExpectedNextNav() {

                final GetNavigationParamsDto nextNav = GetNavigationParamsDto.builder().build();
                final CreateQuestionResponseDto questionResponse = CreateQuestionResponseDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .questionId(QUESTION_ID_1)
                        .build();

                doReturn(nextNav)
                        .when(submissionService).getNextNavigation(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false);

                ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.save(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, questionResponse);

                verify(submissionService).saveQuestionResponse(questionResponse, APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1);
                assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(methodResponse.getBody()).isEqualTo(nextNav);
            }

            @Test
            void save_savesQuestionResponseButCantSaveEvent_shouldReturnFine() {

                final GetNavigationParamsDto nextNav = GetNavigationParamsDto.builder().build();
                final CreateQuestionResponseDto questionResponse = CreateQuestionResponseDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .questionId(QUESTION_ID_1)
                        .build();

                doReturn(nextNav)
                        .when(submissionService).getNextNavigation(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, false);
                doThrow(new RuntimeException()).when(eventLogService).logSubmissionUpdatedEvent(any(), anyString(), eq(SUBMISSION_ID.toString()));

                ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.save(SUBMISSION_ID, SECTION_ID_1, QUESTION_ID_1, questionResponse);

                verify(submissionService).saveQuestionResponse(questionResponse, APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1);
                assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(methodResponse.getBody()).isEqualTo(nextNav);
            }

        }

        @Nested
        class isSubmissionReadyToBeSubmitted {
            @Test
            void isSubmissionReadyToBeSubmitted_ReturnsExpectedResponse_ReturnTrue() {
                when(submissionService.isSubmissionReadyToBeSubmitted(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(true);

                ResponseEntity<Boolean> response = controllerUnderTest.isSubmissionReadyToBeSubmitted(SUBMISSION_ID);


                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(true, response.getBody());
            }

            @Test
            void isSubmissionReadyToBeSubmitted_ReturnsExpectedResponse_ReturnFalse() {
                when(submissionService.isSubmissionReadyToBeSubmitted(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(false);

                ResponseEntity<Boolean> response = controllerUnderTest.isSubmissionReadyToBeSubmitted(SUBMISSION_ID);


                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(false, response.getBody());
            }
        }

        @Nested
        class submitApplication {
            @Test
            void submitApplication_isSuccessfulAndReturnsExpectedResponse() {

                final String emailAddress = "test@email.com";
                final GrantApplicant grantApplicant = GrantApplicant.builder().userId(APPLICANT_USER_ID).id(1).build();
                final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .build();
                final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).email(emailAddress).build();

                when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                when(grantApplicantService.getApplicantFromPrincipal()).thenReturn(grantApplicant);

                ResponseEntity<String> response = controllerUnderTest.submitApplication(submitApplication);

                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                verify(submissionService).submit(submission, grantApplicant, emailAddress);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isEqualTo("Submitted");
            }

            @Test
            void submitApplication_isSuccessfulAndReturnsExpectedResponse_ForV2Schemes() {

                // set up the V2 scheme
                submission.getScheme().setVersion(2);

                final String emailAddress = "test@email.com";
                final GrantApplicant grantApplicant = GrantApplicant.builder().userId(APPLICANT_USER_ID).id(1).build();
                final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .build();
                final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).email(emailAddress).build();

                final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                        .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                        .build();

                when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .thenReturn(jwtPayload);

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                when(grantApplicantService.getApplicantFromPrincipal())
                        .thenReturn(grantApplicant);

                when(mandatoryQuestionService.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submission.getId(), grantApplicant.getUserId()))
                        .thenReturn(mandatoryQuestions);


                final ResponseEntity<String> response = controllerUnderTest.submitApplication(submitApplication);


                verify(mandatoryQuestionService).getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submission.getId(), grantApplicant.getUserId());
                verify(spotlightService).createSpotlightCheck(mandatoryQuestions, submission.getScheme());
                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                verify(submissionService).submit(submission, grantApplicant, emailAddress);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isEqualTo("Submitted");
            }

            @ParameterizedTest
            @EnumSource(value = GrantMandatoryQuestionOrgType.class, names = {"INDIVIDUAL", "OTHER", "LOCAL_AUTHORITY"})
            void submitApplication_DoesNotCreateSpotlightCheck_ForIndividualsOrOther_ForV2Schemes(GrantMandatoryQuestionOrgType orgType) {

                // set up the V2 scheme
                submission.getScheme().setVersion(2);

                final String emailAddress = "test@email.com";
                final GrantApplicant grantApplicant = GrantApplicant.builder().userId(APPLICANT_USER_ID).id(1).build();
                final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .build();
                final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).email(emailAddress).build();

                final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                        .orgType(orgType)
                        .build();

                when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .thenReturn(jwtPayload);

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                when(grantApplicantService.getApplicantFromPrincipal())
                        .thenReturn(grantApplicant);

                when(mandatoryQuestionService.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submission.getId(), grantApplicant.getUserId()))
                        .thenReturn(mandatoryQuestions);


                final ResponseEntity<String> response = controllerUnderTest.submitApplication(submitApplication);


                verify(mandatoryQuestionService).getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submission.getId(), grantApplicant.getUserId());
                verify(spotlightService, never()).createSpotlightCheck(Mockito.any(), Mockito.any());
                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
                verify(submissionService).submit(submission, grantApplicant, emailAddress);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isEqualTo("Submitted");
            }

            @Test
            void submitApplication_ThrowsNotFoundException_IfSubmissionNotFound() {

                final SubmitApplicationDto submitApplication = SubmitApplicationDto.builder()
                        .submissionId(SUBMISSION_ID)
                        .build();

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenThrow(new NotFoundException(""));

                when(grantApplicantService.getApplicantFromPrincipal()).thenReturn(GrantApplicant.builder().userId(APPLICANT_USER_ID).id(1).build());

                assertThrows(NotFoundException.class, () -> controllerUnderTest.submitApplication(submitApplication));
                verify(submissionService).getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID);
            }
        }

        @Nested
        class createApplication {
            @Test
            void createApplication__submissionNotPublished_ThrowException() {
                when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(false);
                GrantApplicationNotPublishedException result = assertThrows(GrantApplicationNotPublishedException.class, () -> controllerUnderTest.createApplication(1));

                assertTrue(result.getMessage().contains(String.format("Grant Application %s is not been published yet.", 1)));
            }

            @Test
            void returnSubmission__IfsubmissionAlreadyExists() throws JsonProcessingException {
                when(securityContext.getAuthentication()).thenReturn(authentication);

                SecurityContextHolder.setContext(securityContext);
                JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();

                when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
                when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(true);
                when(grantApplicationService.getGrantApplicationById(1)).thenReturn(application);
                when(grantApplicantService.getApplicantById(grantApplicant.getUserId())).thenReturn(grantApplicant);
                when(submissionService.getSubmissionByApplicantAndApplicationId(grantApplicant, application))
                        .thenReturn(Optional.of(submission));

                ResponseEntity<CreateSubmissionResponseDto> response = controllerUnderTest.createApplication(1);

                assertEquals(response, new ResponseEntity<>(CreateSubmissionResponseDto.builder().submissionCreated(false)
                        .submissionId(submission.getId())
                        .build(), HttpStatus.OK));
            }

            @Test
            void createApplication_success() throws JsonProcessingException {
                final UUID submissionId = UUID.fromString("1c2eabf0-b33c-433a-b00f-e73d8efca929");

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
                JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
                final String applicantId = jwtPayload.getSub();
                final GrantApplicant grantApplicant = GrantApplicant.builder().userId(applicantId).build();
                when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

                when(grantApplicationService.isGrantApplicationPublished(1)).thenReturn(true);
                when(grantApplicationService.getGrantApplicationById(1)).thenReturn(grantApplication);
                when(grantApplicantService.getApplicantById(applicantId)).thenReturn(grantApplicant);
                when(submissionService.createSubmissionFromApplication(APPLICANT_USER_ID, grantApplicant, grantApplication)).thenReturn(createSubmissionResponseDto);

                ResponseEntity<CreateSubmissionResponseDto> response = controllerUnderTest.createApplication(1);


                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(createSubmissionResponseDto, response.getBody());
            }
        }

        @Nested
        class postSectionReview {
            @Test
            void postSectionReview_completedSectionStatus() {
                final SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().isComplete(true).build();
                when(submissionService.handleSectionReview(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto.getIsComplete())).thenReturn(SubmissionSectionStatus.COMPLETED);
                final ResponseEntity<String> result = controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto);
                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertEquals(result.getBody(), String.format("Section with ID %s status has been updated to %s.", SECTION_ID_1, SubmissionSectionStatus.COMPLETED));
            }

            @Test
            void postSectionReview_inProgressSectionStatus() {
                final SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().isComplete(false).build();
                when(submissionService.handleSectionReview(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto.getIsComplete())).thenReturn(SubmissionSectionStatus.IN_PROGRESS);
                final ResponseEntity<String> result = controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto);
                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertEquals(result.getBody(), String.format("Section with ID %s status has been updated to %s.", SECTION_ID_1, SubmissionSectionStatus.IN_PROGRESS));
            }

            @Test
            void postSectionReview_bodyIsNull() {
                SubmissionReviewBodyDto submissionReviewBodyDto = SubmissionReviewBodyDto.builder().build();
                assertThrows(NullPointerException.class, () -> controllerUnderTest.postSectionReview(SUBMISSION_ID, SECTION_ID_1, submissionReviewBodyDto));
            }
        }

        @Nested
        class postAttachment {
            // it's frightening how simultaneously good and bad this test is
            @Test
            void postAttachment_SavesTheDocumentAndCreatesADatabaseEntry() {

                final String questionId = UUID.randomUUID().toString();

                final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                        .mandatory(true)
                        .allowedTypes(new String[]{"txt"})
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

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                when(submissionService.getNextNavigation(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_2, questionId, false))
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
                        .allowedTypes(new String[]{"txt"})
                        .build();

                final SubmissionQuestion question = SubmissionQuestion.builder()
                        .questionId(questionId)
                        .validation(validation)
                        .build();

                section2.setQuestions(List.of(question));

                when(grantApplicantService.getApplicantFromPrincipal())
                        .thenReturn(grantApplicant);

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, null));

                assertThat(ex.getMessage()).isEqualTo("Select a file to continue");
            }

            @Test
            void postAttachment_ThrowsAttachmentException_IfAttachmentIsEmpty() {

                final String questionId = UUID.randomUUID().toString();

                final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                        .mandatory(true)
                        .allowedTypes(new String[]{"txt"})
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

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file));

                assertThat(ex.getMessage()).isEqualTo("The selected file is empty");
            }

            @Test
            void postAttachment_ThrowsAttachmentException_IfQuestionAlreadyHasAnAttachment() {

                final String questionId = UUID.randomUUID().toString();

                final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                        .mandatory(true)
                        .allowedTypes(new String[]{"txt"})
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

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                final AttachmentException ex = assertThrows(AttachmentException.class, () -> controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file));

                assertThat(ex.getMessage()).isEqualTo("You can only select up to 1 file at the same time");
            }

            @Test
            void postAttachment_SavesTheDocumentCleansFilenameAndCreatesADatabaseEntry() {

                final String questionId = UUID.randomUUID().toString();

                final SubmissionQuestionValidation validation = SubmissionQuestionValidation.builder()
                        .mandatory(true)
                        .allowedTypes(new String[]{"txt"})
                        .build();

                final SubmissionQuestion question = SubmissionQuestion.builder()
                        .questionId(questionId)
                        .validation(validation)
                        .build();

                section2.setQuestions(List.of(question));

                final MultipartFile file = new MockMultipartFile(
                        "file",
                        "<>/?|/:@'*hello.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes()
                );

                final GetNavigationParamsDto expectedNavigation = GetNavigationParamsDto.builder().build();

                when(grantApplicantService.getApplicantFromPrincipal())
                        .thenReturn(grantApplicant);

                when(submissionService.getSubmissionFromDatabaseBySubmissionId(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(submission);

                when(submissionService.getNextNavigation(APPLICANT_USER_ID, SUBMISSION_ID, SECTION_ID_2, questionId, false))
                        .thenReturn(expectedNavigation);

                final ArgumentCaptor<GrantAttachment> attachmentCaptor = ArgumentCaptor.forClass(GrantAttachment.class);

                final ResponseEntity<GetNavigationParamsDto> methodResponse = controllerUnderTest.postAttachment(SUBMISSION_ID, SECTION_ID_2, questionId, file);

                verify(attachmentService).attachmentFile(application.getId() + "/" + SUBMISSION_ID + "/" + questionId + "/" + "__________hello.txt", file);
                verify(grantAttachmentService).createAttachment(attachmentCaptor.capture());
                verify(submissionService).saveSubmission(submission);

                assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(methodResponse.getBody()).isEqualTo(expectedNavigation);
            }
        }

        @Nested
        class isApplicantEligible {
            @Test
            void isApplicantEligible_ReturnsExpectedResponse_ReturnTrue() {
                when(submissionService.isApplicantEligible(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(true);

                final ResponseEntity<Boolean> response = controllerUnderTest.isApplicantEligible(SUBMISSION_ID);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isTrue();
            }


            @Test
            void isApplicantEligible_ReturnsExpectedResponse_ReturnFalse() {
                when(submissionService.isApplicantEligible(APPLICANT_USER_ID, SUBMISSION_ID))
                        .thenReturn(false);

                final ResponseEntity<Boolean> response = controllerUnderTest.isApplicantEligible(SUBMISSION_ID);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isFalse();
            }

            @Test
            void testExportSingleSubmission() throws Exception {
                UUID submissionId = UUID.randomUUID();
                HttpServletRequest mockRequest = new MockHttpServletRequest();
                Submission mockSubmission = mock(Submission.class);
                when(grantApplicantService.getEmailById(any(), any())).thenReturn("mock@example.com");
                when(submissionService.getSubmissionById(any())).thenReturn(mockSubmission);
                when(submissionService.getSubmissionExport(any(), any(), any())).thenReturn(mock(OdfTextDocument.class));
                ByteArrayOutputStream mockOutputSteam = new ByteArrayOutputStream();
                mockOutputSteam.write(1);
                when(zipService.createSubmissionZip(any(), any())).thenReturn(mockOutputSteam);

                when(zipService.byteArrayOutputStreamToResource(any())).thenReturn(new ByteArrayResource(new byte[]{1}));
                ResponseEntity<ByteArrayResource> responseEntity =
                        controllerUnderTest.exportSingleSubmission(submissionId, mockRequest);

                assertEquals("attachment; filename=\"submission.zip\"", Objects.requireNonNull(responseEntity
                        .getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).get(0));
                assertEquals(MediaType.APPLICATION_OCTET_STREAM, responseEntity.getHeaders().getContentType());
                assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).contentLength());
                verify(submissionService, times(1)).getSubmissionExport(any(), any(), any());
                verify(zipService, times(1)).createSubmissionZip(any(), any());
            }

            @Test
            void testExportSingleSubmission_OdtService_throws_ForbiddenException() {
                UUID submissionId = UUID.randomUUID();
                HttpServletRequest mockRequest = new MockHttpServletRequest();
                Submission mockSubmission = mock(Submission.class);
                when(grantApplicantService.getEmailById(any(), any())).thenReturn("mock@example.com");
                when(submissionService.getSubmissionById(any())).thenReturn(mockSubmission);
                when(submissionService.getSubmissionExport(any(), any(), any()))
                        .thenThrow(new ForbiddenException());

                RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> controllerUnderTest
                        .exportSingleSubmission(submissionId, mockRequest));
                assertInstanceOf(ForbiddenException.class, runtimeException.getCause());
                verify(submissionService, times(1)).getSubmissionExport(any(), any(), any());
            }

            @Test
            void testExportSingleSubmission_zipService_throws_IOException() throws Exception {
                UUID submissionId = UUID.randomUUID();
                HttpServletRequest mockRequest = new MockHttpServletRequest();
                Submission mockSubmission = mock(Submission.class);

                when(grantApplicantService.getEmailById(any(), any())).thenReturn("mock@example.com");
                when(submissionService.getSubmissionById(any())).thenReturn(mockSubmission);
                when(submissionService.getSubmissionExport(any(), any(), any()))
                        .thenReturn(mock(OdfTextDocument.class));
                when(zipService.createSubmissionZip(any(), any()))
                        .thenThrow(new IOException("Error creating zip file"));

                RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> controllerUnderTest.exportSingleSubmission(submissionId, mockRequest));

                assertInstanceOf(IOException.class, runtimeException.getCause());
                verify(submissionService, times(1)).getSubmissionExport(any(), any(), any());
                verify(zipService, times(1)).createSubmissionZip(any(), any());
            }


            @Test
            void exportSingleSubmissionThrowsRuntimeError() {
                UUID submissionID = UUID.randomUUID();
                final MockHttpServletRequest request = new MockHttpServletRequest();
                when(submissionService.getSubmissionExport(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                        .thenThrow(new ForbiddenException());

                assertThrows(RuntimeException.class, () -> controllerUnderTest.exportSingleSubmission(submissionID, request));
            }
        }

        @Nested
        class ApplicationStatus {
            @Test
            void applicationStatus_ReturnsStatusForMatchingApplicantId() {
                when(submissionService.getOptionalSubmissionById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

                final ResponseEntity<String> response = controllerUnderTest.applicationStatus(SUBMISSION_ID);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(GrantApplicationStatus.PUBLISHED.name(), response.getBody());
            }

            @Test
            void applicationStatus_ReturnsNotFoundForNonexistentSubmissionId() {
                when(submissionService.getOptionalSubmissionById(SUBMISSION_ID)).thenReturn(Optional.empty());

                final ResponseEntity<String> response = controllerUnderTest.applicationStatus(SUBMISSION_ID);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            }

            @Test
            void applicationStatus_ReturnsUnauthorizedForNonMatchingApplicantId() {
                final GrantApplicant grantApplicant = GrantApplicant.builder().userId("aDifferentId").build();
                final Submission submission = Submission
                        .builder()
                        .id(SUBMISSION_ID)
                        .applicant(grantApplicant)
                        .build();

                when(submissionService.getOptionalSubmissionById(SUBMISSION_ID)).thenReturn(Optional.of(submission));

                final ResponseEntity<String> response = controllerUnderTest.applicationStatus(SUBMISSION_ID);

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }

    }


    @Nested
    class withoutSecurityContext {
        @Nested
        class updateAttachment {
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

                when(submissionService.getSubmissionById(SUBMISSION_ID))
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


        }
    }
}