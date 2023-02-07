package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantApplicantServiceTest {

    private final UUID APPLICANT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
    @Mock
    private GrantApplicantRepository grantApplicantRepository;
    @InjectMocks
    private GrantApplicantService serviceUnderTest;

    @Test
    void getApplicantById_Success() {
        final GrantApplicant applicant = GrantApplicant.builder()
                .id(1)
                .userId(APPLICANT_ID)
                .build();

        when(grantApplicantRepository.findByUserId(APPLICANT_ID)).thenReturn(Optional.of(applicant));

        GrantApplicant methodResponse = serviceUnderTest.getApplicantById(APPLICANT_ID);

        verify(grantApplicantRepository).findByUserId(APPLICANT_ID);
        assertEquals(methodResponse, applicant);
    }

    @Test
    void getOrganisationById_OrgNotFound() {
        when(grantApplicantRepository.findByUserId(APPLICANT_ID)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.getApplicantById(APPLICANT_ID));
        verify(grantApplicantRepository).findByUserId(APPLICANT_ID);
        assertTrue(result.getMessage().contains(String.format("No Grant Applicant with ID %s was found", APPLICANT_ID)));
    }


    @Test
    void createApplicant() {
        final ArgumentCaptor<GrantApplicant> grantApplicantArgumentCaptor = ArgumentCaptor
                .forClass(GrantApplicant.class);
        UUID userId = UUID.randomUUID();
        GrantApplicant grantApplicant = GrantApplicant.builder().id(1L).userId(userId).build();

        serviceUnderTest.saveApplicant(grantApplicant);

        verify(grantApplicantRepository).save(grantApplicantArgumentCaptor.capture());

        final GrantApplicant capturedApplicant = grantApplicantArgumentCaptor.getValue();

        assertThat(capturedApplicant.getUserId())
                .isEqualTo(grantApplicant.getUserId());
        assertThat(capturedApplicant.getId())
                .isEqualTo(grantApplicant.getId());
    }
}