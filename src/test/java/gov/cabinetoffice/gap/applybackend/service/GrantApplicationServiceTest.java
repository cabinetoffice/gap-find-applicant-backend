package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantApplicationServiceTest {

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

    @Test
    void getGrantApplicationByGrantScheme__NotFound() {
        final int schemeId = 1;

        when(grantApplicationRepository.getGrantApplicationByGrantSchemeId(schemeId)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.getGrantApplicationByGrantScheme(schemeId));
        verify(grantApplicationRepository).getGrantApplicationByGrantSchemeId(schemeId);

        assertTrue(result.getMessage().contains("No Application with scheme ID " + schemeId + " was found"));
    }

    @Test
    void getGrantApplicationById__Success() {
        final GrantApplication application = GrantApplication.builder()
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        final GrantApplication methodResponse = serviceUnderTest.getGrantApplicationById(1);

        verify(grantApplicationRepository).findById(1);
        assertEquals(methodResponse, application);
    }


    @Test
    void isGrantApplicationPublished__True() {
        final GrantApplication application = GrantApplication.builder()
                .applicationStatus(GrantApplicationStatus.PUBLISHED)
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        final boolean response = serviceUnderTest.isGrantApplicationPublished(1);

        verify(grantApplicationRepository).findById(1);
        assertTrue(response);
    }

    @Test
    void isGrantApplicationPublished__False() {
        final GrantApplication application = GrantApplication.builder()
                .applicationStatus(GrantApplicationStatus.DRAFT)
                .id(1)
                .build();

        when(grantApplicationRepository.findById(1)).thenReturn(Optional.of(application));

        final boolean response = serviceUnderTest.isGrantApplicationPublished(1);

        verify(grantApplicationRepository).findById(1);
        assertFalse(response);
    }

    @Test
    void doesSchemeHavePublishedApplication__True() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantApplication application = GrantApplication.builder()
                .grantScheme(scheme)
                .applicationStatus(GrantApplicationStatus.PUBLISHED)
                .build();

        when(grantApplicationRepository.findByGrantSchemeAndApplicationStatus(scheme, GrantApplicationStatus.PUBLISHED))
                .thenReturn(Optional.of(application));

        final boolean response = serviceUnderTest.doesSchemeHaveAPublishedApplication(scheme);

        verify(grantApplicationRepository).findByGrantSchemeAndApplicationStatus(scheme, GrantApplicationStatus.PUBLISHED);
        assertTrue(response);
    }

    @Test
    void doesSchemeHavePublishedApplication__False() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();

        when(grantApplicationRepository.findByGrantSchemeAndApplicationStatus(scheme, GrantApplicationStatus.PUBLISHED)).thenReturn(Optional.empty());

        final boolean response = serviceUnderTest.doesSchemeHaveAPublishedApplication(scheme);

        verify(grantApplicationRepository).findByGrantSchemeAndApplicationStatus(scheme, GrantApplicationStatus.PUBLISHED);
        assertFalse(response);
    }

    @Test
    void doesSchemeHaveApplication__True() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantApplication application = GrantApplication.builder()
                .grantScheme(scheme)
                .build();

        when(grantApplicationRepository.findByGrantScheme(scheme))
                .thenReturn(Optional.of(application));

        final boolean response = serviceUnderTest.doesSchemeHaveAnApplication(scheme);

        verify(grantApplicationRepository).findByGrantScheme(scheme);
        assertTrue(response);
    }

    @Test
    void doesSchemeHaveApplication__False() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();

        when(grantApplicationRepository.findByGrantScheme(scheme)).thenReturn(Optional.empty());

        final boolean response = serviceUnderTest.doesSchemeHaveAnApplication(scheme);

        verify(grantApplicationRepository).findByGrantScheme(scheme);
        assertFalse(response);
    }
    @Test
    void getGrantApplicationId__returnsId() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantApplication application = GrantApplication.builder().id(1).grantScheme(scheme)
                .build();

        when(grantApplicationRepository.findByGrantScheme(scheme)).thenReturn(Optional.of(application));

        final Integer response = serviceUnderTest.getGrantApplicationId(scheme);

        verify(grantApplicationRepository).findByGrantScheme(scheme);
        assertThat(response).isEqualTo(1);
    }

    @Test
    void getGrantApplicationId__returnsNull() {
        final GrantScheme scheme = GrantScheme.builder().id(1).build();

        when(grantApplicationRepository.findByGrantScheme(scheme)).thenReturn(Optional.empty());

        final Integer response = serviceUnderTest.getGrantApplicationId(scheme);

        verify(grantApplicationRepository).findByGrantScheme(scheme);
        assertNull(response);
    }
}
