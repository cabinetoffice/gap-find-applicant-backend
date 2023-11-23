package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeWithApplicationAndAdverts;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.mapper.GrantSchemeMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantSchemeControllerTest {
    private final Integer SCHEME_ID = 1;
    @Mock
    private GrantSchemeService grantSchemeService;
    @Mock
    private GrantSchemeMapper grantSchemeMapper;
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
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub("sub").build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
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

        when(grantSchemeService.getSchemeById(SCHEME_ID)).thenReturn(grantScheme);

        ResponseEntity<GetGrantSchemeWithApplicationAndAdverts> response = controllerUnderTest.getGrantSchemeById(SCHEME_ID);

        verify(grantSchemeService).getSchemeById(SCHEME_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(GetGrantSchemeWithApplicationAndAdverts.builder()
                .grantScheme(getGrantSchemeDto)
                .grantAdverts(Collections.emptyList())
                .grantApplication(null)
                .build(), response.getBody());
    }
}
