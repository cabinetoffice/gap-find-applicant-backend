package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeWithApplicationAndAdverts;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.SchemeMandatoryQuestionApplicationFormInfosDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertStatus;
import gov.cabinetoffice.gap.applybackend.mapper.GrantSchemeMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantSchemeControllerTest {
    private final Integer SCHEME_ID = 1;
    @Mock
    private GrantSchemeService grantSchemeService;
    @Mock
    private GrantSchemeMapper grantSchemeMapper;
    @Mock
    private GrantApplicationService grantApplicationService;
    @Mock
    private GrantAdvertService grantAdvertService;
    @Mock
    private EnvironmentProperties  environmentProperties;
    @InjectMocks
    private GrantSchemeController controllerUnderTest;
    @MockBean
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    private JwtPayload jwtPayload;

    @BeforeEach
    void setup() {

    }

    @Test
    void getGrantSchemeById_ReturnsTheCorrectGrantScheme() {
        final GrantScheme grantScheme = GrantScheme.builder()
                .id(SCHEME_ID)
                .funderId(1)
                .version(1)
                .lastUpdated(Instant.now())
                .lastUpdatedBy(1)
                .ggisIdentifier("SCH-000003589")
                .name("scheme_name")
                .email("contact@contact.com")
                .grantAdverts(Collections.emptyList())
                .grantApplication(GrantApplication.builder().build())
                .build();
        final GetGrantSchemeDto getGrantSchemeDto = new GetGrantSchemeDto(grantScheme);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub("sub").build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(grantSchemeService.getSchemeByIdWithApplicationAndAdverts(SCHEME_ID)).thenReturn(grantScheme);

        final ResponseEntity<GetGrantSchemeWithApplicationAndAdverts> response = controllerUnderTest.getGrantSchemeById(SCHEME_ID);

        verify(grantSchemeService).getSchemeByIdWithApplicationAndAdverts(SCHEME_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(GetGrantSchemeWithApplicationAndAdverts.builder()
                .grantScheme(getGrantSchemeDto)
                .grantAdverts(Collections.emptyList())
                .grantApplication(null)
                .build(), response.getBody());
    }

    @Test
    void getGrantSchemeById_ReturnsTheCorrectGrantSchemeWithNoAdverts() {
        final GrantAdvert grantAdvert = GrantAdvert.builder().status(GrantAdvertStatus.DRAFT).build();
        final GrantScheme grantScheme = GrantScheme.builder()
                .id(SCHEME_ID)
                .funderId(1)
                .version(1)
                .lastUpdated(Instant.now())
                .lastUpdatedBy(1)
                .ggisIdentifier("SCH-000003589")
                .name("scheme_name")
                .email("contact@contact.com")
                .grantAdverts(Collections.singletonList(grantAdvert))
                .grantApplication(GrantApplication.builder().build())
                .build();
        final GetGrantSchemeDto getGrantSchemeDto = new GetGrantSchemeDto(grantScheme);

        when(grantSchemeService.getSchemeByIdWithApplicationAndAdverts(SCHEME_ID)).thenReturn(grantScheme);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub("sub").build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

        final ResponseEntity<GetGrantSchemeWithApplicationAndAdverts> response = controllerUnderTest.getGrantSchemeById(SCHEME_ID);

        verify(grantSchemeService).getSchemeByIdWithApplicationAndAdverts(SCHEME_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(GetGrantSchemeWithApplicationAndAdverts.builder()
                .grantScheme(getGrantSchemeDto)
                .grantAdverts(Collections.emptyList())
                .grantApplication(null)
                .build(), response.getBody());
    }

    @Test
    void schemeHasInternalApplicationAndHasPublishedApplicationForm(){
        final GrantScheme grantScheme = GrantScheme.builder()
                .id(SCHEME_ID).build();
        final GrantAdvert grantAdvert = GrantAdvert.builder().scheme(grantScheme).build();
        final String webPageUrl = "http://localhost:3000/apply/applicant/internalUrl";
        final String frontEndUrl = "http://localhost:3000/apply/applicant";

        when(grantSchemeService.getSchemeById(SCHEME_ID)).thenReturn(grantScheme);
        when(grantAdvertService.getAdvertBySchemeId(SCHEME_ID.toString())).thenReturn(grantAdvert);
        when(grantAdvertService.getExternalSubmissionUrl(grantAdvert)).thenReturn(webPageUrl);
        when(environmentProperties.getFrontEndUri()).thenReturn(frontEndUrl);
        when(grantApplicationService.doesSchemeHaveAPublishedApplication(grantScheme)).thenReturn(true);

        final ResponseEntity<SchemeMandatoryQuestionApplicationFormInfosDto> response = controllerUnderTest.schemeHasInternalApplication(SCHEME_ID);

        assertTrue(response.getBody().isHasInternalApplication());
        assertTrue(response.getBody().isHasPublishedInternalApplication());
    }

    @Test
    void schemeHasInternalApplicationAndHasNotPublishedApplicationForm(){
        final GrantScheme grantScheme = GrantScheme.builder()
                .id(SCHEME_ID).build();
        final GrantAdvert grantAdvert = GrantAdvert.builder().scheme(grantScheme).build();
        final String webPageUrl = "http://localhost:3000/apply/applicant/internalUrl";
        final String frontEndUrl = "http://localhost:3000/apply/applicant";

        when(grantSchemeService.getSchemeById(SCHEME_ID)).thenReturn(grantScheme);
        when(grantAdvertService.getAdvertBySchemeId(SCHEME_ID.toString())).thenReturn(grantAdvert);
        when(grantAdvertService.getExternalSubmissionUrl(grantAdvert)).thenReturn(webPageUrl);
        when(environmentProperties.getFrontEndUri()).thenReturn(frontEndUrl);
        when(grantApplicationService.doesSchemeHaveAPublishedApplication(grantScheme)).thenReturn(false);

        final ResponseEntity<SchemeMandatoryQuestionApplicationFormInfosDto> response = controllerUnderTest.schemeHasInternalApplication(SCHEME_ID);

        assertTrue(response.getBody().isHasInternalApplication());
        assertFalse(response.getBody().isHasPublishedInternalApplication());
    }

    @Test
    void schemeHasNotInternalApplicationAndHasNotPublishedApplicationForm(){
        final GrantScheme grantScheme = GrantScheme.builder()
                .id(SCHEME_ID).build();
        final GrantAdvert grantAdvert = GrantAdvert.builder().scheme(grantScheme).build();
        final String webPageUrl = "http://externalURL";
        final String frontEndUrl = "http://localhost:3000/apply/applicant";

        when(grantSchemeService.getSchemeById(SCHEME_ID)).thenReturn(grantScheme);
        when(grantAdvertService.getAdvertBySchemeId(SCHEME_ID.toString())).thenReturn(grantAdvert);
        when(grantAdvertService.getExternalSubmissionUrl(grantAdvert)).thenReturn(webPageUrl);
        when(environmentProperties.getFrontEndUri()).thenReturn(frontEndUrl);

        final ResponseEntity<SchemeMandatoryQuestionApplicationFormInfosDto> response = controllerUnderTest.schemeHasInternalApplication(SCHEME_ID);

        verify(grantApplicationService, never()).doesSchemeHaveAPublishedApplication(grantScheme);
        assertFalse(response.getBody().isHasInternalApplication());
        assertFalse(response.getBody().isHasPublishedInternalApplication());
    }

}
