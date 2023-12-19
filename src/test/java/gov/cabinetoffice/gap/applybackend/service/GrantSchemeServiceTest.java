package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantSchemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantSchemeServiceTest {

    @Mock
    private GrantSchemeRepository grantSchemeRepository;

    @InjectMocks
    private GrantSchemeService serviceUnderTest;
    private final Integer SCHEME_ID = 1;

    @Test
    void getSchemeById_Success() {
        final GrantScheme scheme = GrantScheme.builder()
                .id(SCHEME_ID)
                .funderId(1)
                .version(1)
                .lastUpdated(Instant.now())
                .lastUpdatedBy(1)
                .ggisIdentifier("SCH-000003589")
                .name("scheme_name")
                .email("contact@contact.com")
                .build();

        when(grantSchemeRepository.findById(SCHEME_ID)).thenReturn(Optional.of(scheme));

        GrantScheme methodResponse = serviceUnderTest.getSchemeById(SCHEME_ID);

        verify(grantSchemeRepository).findById(SCHEME_ID);
        assertEquals(methodResponse, scheme);
    }

    @Test
    void getGrantSchemeById_OrgNotFound() {
        when(grantSchemeRepository.findById(SCHEME_ID)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.getSchemeById(SCHEME_ID));
        verify(grantSchemeRepository).findById(SCHEME_ID);

        assertTrue(result.getMessage().contains("No Grant Scheme with ID " + SCHEME_ID + " was found"));
    }
}