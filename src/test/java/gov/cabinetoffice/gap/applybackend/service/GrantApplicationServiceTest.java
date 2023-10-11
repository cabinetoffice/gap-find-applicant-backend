package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantApplicationServiceTest {

    private final UUID APPLICANT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
    @Mock
    private GrantApplicationRepository grantApplicationRepository;
    @InjectMocks
    private GrantApplicationService serviceUnderTest;

    @Test
    void getGrantApplicationByGrantScheme__Success() {
        final GrantApplication application = GrantApplication.builder()
                .id(1)
                .build();
        when(grantApplicationRepository.getGrantApplicationByGrantSchemeId(1)).thenReturn(Optional.of(application));
        GrantApplication methodResponse = serviceUnderTest.getGrantApplicationByGrantScheme(1);

        verify(grantApplicationRepository).getGrantApplicationByGrantSchemeId(1);
        assertEquals(methodResponse, application);
    }

    void getGrantApplicationByGrantScheme__NotFound() {
        when(grantApplicationRepository.getGrantApplicationByGrantSchemeId(1)).thenReturn(Optional.empty());

        verify(grantApplicationRepository).getGrantApplicationByGrantSchemeId(1);
        assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantApplicationByGrantScheme(1));
    }


    @Test
    void getGrantApplicationById__Success() {
        final GrantApplication application = GrantApplication.builder()
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        GrantApplication methodResponse = serviceUnderTest.getGrantApplicationById(1);

        verify(grantApplicationRepository).findById(1);
        assertEquals(methodResponse, application);
    }


    @Test
    void isGrantApplicationPublished__True() {
        final GrantApplication application = GrantApplication.builder()
                .applicationStatus(GrantApplicantStatus.PUBLISHED)
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        boolean response = serviceUnderTest.isGrantApplicationPublished(1);

        verify(grantApplicationRepository).findById(1);
        assertTrue(response);
    }

    @Test
    void isGrantApplicationPublished__False() {
        final GrantApplication application = GrantApplication.builder()
                .applicationStatus(GrantApplicantStatus.DRAFT)
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        boolean response = serviceUnderTest.isGrantApplicationPublished(1);

        verify(grantApplicationRepository).findById(1);
        assertFalse(response);
    }
}
