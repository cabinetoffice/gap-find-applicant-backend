package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantSchemeControllerTest {
    private final Integer SCHEME_ID = 1;
    @Mock
    private GrantSchemeService grantSchemeService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private GrantSchemeController controllerUnderTest;

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
                .build();

        when(grantSchemeService.getSchemeById(SCHEME_ID))
                .thenReturn(grantScheme);

        ResponseEntity<GrantScheme> response = controllerUnderTest.getGrantSchemeById(SCHEME_ID);

        verify(grantSchemeService).getSchemeById(SCHEME_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), grantScheme);
    }
}
