package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.mapper.GrantMandatoryQuestionMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import gov.cabinetoffice.gap.applybackend.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation.SCOTLAND;
import static gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionsControllerTest {

    private final String applicantUserId = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
    private final GrantApplicantOrganisationProfile applicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
            .id(1)
            .build();
    private final GrantApplicant applicant = GrantApplicant.builder()
            .id(1)
            .userId(applicantUserId)
            .organisationProfile(applicantOrganisationProfile)
            .build();

    private final int schemeId = 123;

    private final GrantScheme scheme = GrantScheme.builder()
            .id(schemeId)
            .build();

    private final UUID MANDATORY_QUESTION_ID = UUID.fromString("8e33d655-556e-49d5-bc46-3cfa4fdfa00f");

    @Mock
    private GrantMandatoryQuestionService grantMandatoryQuestionService;
    @Mock
    private GrantMandatoryQuestionMapper grantMandatoryQuestionMapper;
    @Mock
    private GrantApplicantService grantApplicantService;
    @Mock
    private GrantSchemeService grantSchemeService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private GrantMandatoryQuestionsController controllerUnderTest;

    private JwtPayload jwtPayload;

    @BeforeEach
    void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub(applicantUserId).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
    }

    @Test
    void createMandatoryQuestion_CreatesMandatoryQuestionEntry_AndReturnsItsID() {

        final GrantMandatoryQuestions emptyMandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .build();
        final GetGrantMandatoryQuestionDto emptyMandatoryQuestionsDto = GetGrantMandatoryQuestionDto.builder()
                .id(MANDATORY_QUESTION_ID)
                .build();

        when(grantApplicantService.getApplicantById(applicantUserId))
                .thenReturn(applicant);

        when(grantSchemeService.getSchemeById(schemeId))
                .thenReturn(scheme);

        when(grantMandatoryQuestionService.createMandatoryQuestion(scheme, applicant))
                .thenReturn(emptyMandatoryQuestions);

        when(grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(emptyMandatoryQuestions))
                .thenReturn(emptyMandatoryQuestionsDto);

        final ResponseEntity<GetGrantMandatoryQuestionDto> methodResponse = controllerUnderTest.createMandatoryQuestion(schemeId);

        verify(grantMandatoryQuestionService).createMandatoryQuestion(scheme, applicant);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(emptyMandatoryQuestionsDto);

    }

    @Test
    void getGrantMandatoryQuestionsById_ReturnsExpectedMandatoryQuestions() {
        final GrantMandatoryQuestionFundingLocation fundingLocation = SCOTLAND;
        final GrantMandatoryQuestionFundingLocation[] fundingLocations = new GrantMandatoryQuestionFundingLocation[]{fundingLocation};
        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .fundingAmount(new BigDecimal("50000.00"))
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(fundingLocations)
                .companiesHouseNumber("08761455")
                .orgType(LIMITED_COMPANY)
                .build();

        final GetGrantMandatoryQuestionDto mandatoryQuestionsDto = GetGrantMandatoryQuestionDto.builder()
                .name("AND Digital")
                .fundingAmount("50000.00")
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(List.of("Scotland"))
                .companiesHouseNumber("08761455")
                .orgType("Limited company")
                .schemeId(scheme.getId())
                .build();

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(mandatoryQuestions))
                .thenReturn(mandatoryQuestionsDto);

        final ResponseEntity<GetGrantMandatoryQuestionDto> methodResponse = controllerUnderTest.getGrantMandatoryQuestionsById(MANDATORY_QUESTION_ID);

        verify(grantMandatoryQuestionService).getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub());
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(mandatoryQuestionsDto);
    }

    @Test
    void updateMandatoryQuestion_UpdatesExpectedFields_AndSavesChanges() {
        final GrantMandatoryQuestionFundingLocation fundingLocation = SCOTLAND;
        final GrantMandatoryQuestionFundingLocation[] fundingLocations = new GrantMandatoryQuestionFundingLocation[]{fundingLocation};
        final Optional<String> updatedValue = Optional.of("AND Digital updated");
        final UpdateGrantMandatoryQuestionDto updateDto = UpdateGrantMandatoryQuestionDto.builder()
                .name(updatedValue)
                .build();

        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .fundingAmount(new BigDecimal("50000.00"))
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(fundingLocations)
                .companiesHouseNumber("08761455")
                .orgType(LIMITED_COMPANY)
                .build();

        when(grantApplicantService.getApplicantById(jwtPayload.getSub()))
                .thenReturn(applicant);

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateDto, mandatoryQuestions))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.updateMandatoryQuestion(mandatoryQuestions, applicant))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.generateNextPageUrl("url", MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn("nextPageUrl");


        final ResponseEntity<String> methodResponse = controllerUnderTest.updateMandatoryQuestion(MANDATORY_QUESTION_ID, updateDto, "url");

        verify(grantMandatoryQuestionService).addMandatoryQuestionsToSubmissionObject(mandatoryQuestions);
        verify(grantMandatoryQuestionService).updateMandatoryQuestion(mandatoryQuestions, applicant);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("nextPageUrl");

    }

    @Test
    void updateMandatoryQuestion_AddsSubmissionToMandatoryQuestions_IfSubmissionIdProvided() {

        final UUID submissionId = UUID.randomUUID();
        final UpdateGrantMandatoryQuestionDto updateDto = UpdateGrantMandatoryQuestionDto.builder()
                .submissionId(submissionId)
                .build();

        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .build();

        final Submission submission = Submission.builder()
                .id(submissionId)
                .build();

        when(grantApplicantService.getApplicantById(jwtPayload.getSub()))
                .thenReturn(applicant);

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateDto, mandatoryQuestions))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.updateMandatoryQuestion(mandatoryQuestions, applicant))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.generateNextPageUrl("url", MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn("nextPageUrl");

        when(submissionService.getSubmissionFromDatabaseBySubmissionId(jwtPayload.getSub(), submissionId))
                .thenReturn(submission);


        final ResponseEntity<String> methodResponse = controllerUnderTest.updateMandatoryQuestion(MANDATORY_QUESTION_ID, updateDto, "url");


        verify(submissionService).getSubmissionFromDatabaseBySubmissionId(jwtPayload.getSub(), submissionId);
        verify(grantMandatoryQuestionService).updateMandatoryQuestion(mandatoryQuestions, applicant);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("nextPageUrl");
    }

    @Test
    void updateMandatoryQuestion_SetsStatusToComplete() {
        final Optional<String> updatedValue = Optional.of("AND Digital updated");
        final UpdateGrantMandatoryQuestionDto updateDto = UpdateGrantMandatoryQuestionDto.builder()
                .name(updatedValue)
                .mandatoryQuestionsComplete(true)
                .build();

        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .status(GrantMandatoryQuestionStatus.IN_PROGRESS)
                .build();

        when(grantApplicantService.getApplicantById(jwtPayload.getSub()))
                .thenReturn(applicant);

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateDto, mandatoryQuestions))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.updateMandatoryQuestion(mandatoryQuestions, applicant))
                .thenReturn(mandatoryQuestions);

        when(grantMandatoryQuestionService.generateNextPageUrl("url", MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn("nextPageUrl");

        final ResponseEntity<String> methodResponse = controllerUnderTest.updateMandatoryQuestion(MANDATORY_QUESTION_ID, updateDto, "url");

        verify(grantMandatoryQuestionService).addMandatoryQuestionsToSubmissionObject(mandatoryQuestions);
        verify(grantMandatoryQuestionService).updateMandatoryQuestion(mandatoryQuestions, applicant);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("nextPageUrl");
        assertThat(mandatoryQuestions.getStatus()).isEqualTo(GrantMandatoryQuestionStatus.COMPLETED);

    }


    @ParameterizedTest
    @EnumSource(value = GrantMandatoryQuestionOrgType.class, names = {"INDIVIDUAL", "OTHER", "LOCAL_AUTHORITY"})
    void updateMandatoryQuestion_setCharityAndCommissionNumberAsNullIfOrgTypeIsLocalAuthority(GrantMandatoryQuestionOrgType orgType) {
        final String orgTypeString = orgType.toString();
        final UpdateGrantMandatoryQuestionDto updateDto = UpdateGrantMandatoryQuestionDto.builder()
                .orgType(Optional.of(orgTypeString))
                .build();

        final GrantMandatoryQuestions mandatoryQuestionsBefore = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .companiesHouseNumber("08761455")
                .charityCommissionNumber("123456")
                .build();

        final GrantMandatoryQuestions mandatoryQuestionsAfter = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .orgType(orgType)
                .companiesHouseNumber(null)
                .charityCommissionNumber(null)
                .build();

        when(grantApplicantService.getApplicantById(jwtPayload.getSub()))
                .thenReturn(applicant);

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestionsBefore);

        when(grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateDto, mandatoryQuestionsBefore))
                .thenReturn(mandatoryQuestionsBefore);

        when(grantMandatoryQuestionService.updateMandatoryQuestion(mandatoryQuestionsBefore, applicant))
                .thenReturn(mandatoryQuestionsAfter);

        when(grantMandatoryQuestionService.generateNextPageUrl("url", MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn("nextPageUrl");


        final ResponseEntity<String> methodResponse = controllerUnderTest.updateMandatoryQuestion(MANDATORY_QUESTION_ID, updateDto, "url");

        verify(grantMandatoryQuestionService).addMandatoryQuestionsToSubmissionObject(mandatoryQuestionsBefore);
        verify(grantMandatoryQuestionService).updateMandatoryQuestion(mandatoryQuestionsBefore, applicant);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("nextPageUrl");

    }


    @Test
    void shouldReturnMandatoryQuestionsIfValidSchemeAndUserIdIsGiven() {
        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .fundingAmount(new BigDecimal("50000.00"))
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .companiesHouseNumber("08761455")
                .orgType(LIMITED_COMPANY)
                .build();

        final GetGrantMandatoryQuestionDto mandatoryQuestionsDto = GetGrantMandatoryQuestionDto.builder()
                .name("AND Digital")
                .fundingAmount("50000.00")
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(List.of("Scotland"))
                .companiesHouseNumber("08761455")
                .orgType("Limited company")
                .schemeId(scheme.getId())
                .build();

        when(grantMandatoryQuestionService.getMandatoryQuestionBySchemeId(1, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);
        when(grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(mandatoryQuestions))
                .thenReturn(mandatoryQuestionsDto);

        final ResponseEntity<GetGrantMandatoryQuestionDto> methodResponse = controllerUnderTest
                .getGrantMandatoryQuestionsBySchemeId(1);

        verify(grantMandatoryQuestionService).getMandatoryQuestionBySchemeId(1, jwtPayload.getSub());
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(mandatoryQuestionsDto);

    }

    @Test
    void shouldReturnTrueIfUserHasMandatoryQuestionsForValidSchemeId() {

        when(grantApplicantService.getApplicantById(jwtPayload.getSub())).thenReturn(applicant);

        when(grantMandatoryQuestionService.mandatoryQuestionExistsBySchemeIdAndApplicantId(1, 1L)).thenReturn(true);

        final ResponseEntity<Boolean> methodResponse = controllerUnderTest
                .existsBySchemeIdAndApplicantId(1);

        verify(grantMandatoryQuestionService).mandatoryQuestionExistsBySchemeIdAndApplicantId(1, 1L);
        assertThat(methodResponse.getBody()).isEqualTo(Boolean.TRUE);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void shouldReturnApplicationStatusFromQuestionUUID() {

        final GrantApplication application = GrantApplication.builder()
                .id(1)
                .applicationStatus(GrantApplicationStatus.REMOVED)
                .build();
        final GrantScheme scheme = GrantScheme.builder()
                .id(schemeId)
                .grantApplication(application)
                .build();

        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(MANDATORY_QUESTION_ID)
                .grantScheme(scheme)
                .createdBy(applicant)
                .build();

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(MANDATORY_QUESTION_ID, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        final ResponseEntity<String> methodResponse = controllerUnderTest.getApplicationStatusByMandatoryQuestionId(MANDATORY_QUESTION_ID);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("REMOVED");
    }
}