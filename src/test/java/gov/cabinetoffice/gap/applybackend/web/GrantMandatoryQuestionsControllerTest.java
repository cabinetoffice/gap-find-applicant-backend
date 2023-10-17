package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class GrantMandatoryQuestionsControllerTest {

    private final String applicantUserId ="75ab5fbd-0682-4d3d-a467-01c7a447f07c";

    private final GrantApplicant applicant = GrantApplicant.builder()
            .id(1)
            .userId(applicantUserId)
            .build();

    private final int schemeId = 123;

    private final GrantScheme scheme = GrantScheme.builder()
            .id(schemeId)
            .build();

    @Mock private GrantMandatoryQuestionService grantMandatoryQuestionService;
    @Mock private GrantApplicantService grantApplicantService;
    @Mock private GrantSchemeService grantSchemeService;
    @Mock private ModelMapper modelMapper;

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private GrantMandatoryQuestionsController controllerUnderTest;

    private JwtPayload jwtPayload;

    @BeforeEach
    void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub(String.valueOf(applicantUserId)).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
    }

    @Test
    void createMandatoryQuestion_CreatesMandatoryQuestionEntry_AndReturnsItsID() {

        final UUID mandatoryQuestionsId = UUID.randomUUID();
        final GrantMandatoryQuestions emptyMandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(mandatoryQuestionsId)
                .build();

        when(grantApplicantService.getApplicantById(applicantUserId))
                .thenReturn(applicant);

        when(grantSchemeService.getSchemeById(schemeId))
                .thenReturn(scheme);

        when(grantMandatoryQuestionService.createMandatoryQuestion(scheme, applicant))
                .thenReturn(emptyMandatoryQuestions);

        final ResponseEntity<UUID> methodResponse = controllerUnderTest.createMandatoryQuestion(schemeId);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(emptyMandatoryQuestions.getId());

    }

    @Test
    void getGrantMandatoryQuestionsById_ReturnsExpectedMandatoryQuestions() {

        final UUID mandatoryQuestionsId = UUID.randomUUID();
        final GrantMandatoryQuestionFundingLocation fundingLocation = GrantMandatoryQuestionFundingLocation.SCOTLAND;
        final GrantMandatoryQuestionFundingLocation[] fundingLocations = new GrantMandatoryQuestionFundingLocation[]{fundingLocation};
        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(mandatoryQuestionsId)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .fundingAmount(new BigDecimal("50000.00"))
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(fundingLocations)
                .companiesHouseNumber("08761455")
                .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
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

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionsId, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        when(modelMapper.map(mandatoryQuestions, GetGrantMandatoryQuestionDto.class))
                .thenReturn(mandatoryQuestionsDto);

        final ResponseEntity<GetGrantMandatoryQuestionDto> methodResponse = controllerUnderTest.getGrantMandatoryQuestionsById(mandatoryQuestionsId);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(mandatoryQuestionsDto);
    }

    @Test
    void updateMandatoryQuestion_UpdatesExpectedFields_AndSavesChanges() {
        final GrantMandatoryQuestionFundingLocation fundingLocation = GrantMandatoryQuestionFundingLocation.SCOTLAND;
        final GrantMandatoryQuestionFundingLocation[] fundingLocations = new GrantMandatoryQuestionFundingLocation[]{fundingLocation};
        final String updatedValue = "AND Digital updated";
        final UpdateGrantMandatoryQuestionDto updateDto = UpdateGrantMandatoryQuestionDto.builder()
                .name(updatedValue)
                .build();

        final UUID mandatoryQuestionsId = UUID.randomUUID();

        final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                .id(mandatoryQuestionsId)
                .createdBy(applicant)
                .grantScheme(scheme)
                .name("AND Digital")
                .fundingAmount(new BigDecimal("50000.00"))
                .addressLine1("215 Bothwell Street")
                .city("Glasgow")
                .postcode("G2 7EZ")
                .fundingLocation(fundingLocations)
                .companiesHouseNumber("08761455")
                .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                .build();

        when(grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionsId, jwtPayload.getSub()))
                .thenReturn(mandatoryQuestions);

        final ResponseEntity<String> methodResponse = controllerUnderTest.updateMandatoryQuestion(mandatoryQuestionsId, updateDto);

        verify(modelMapper).map(updateDto, mandatoryQuestions);
        verify(grantMandatoryQuestionService).updateMandatoryQuestion(mandatoryQuestions);

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo(String.format("Mandatory question with ID %s has been updated.", mandatoryQuestionsId));

    }

}