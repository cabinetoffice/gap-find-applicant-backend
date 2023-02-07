package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.FundingOrganisation;
import gov.cabinetoffice.gap.applybackend.repository.FundingOrganisationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundingOrganisationServiceTest {

    @Mock
    private FundingOrganisationRepository fundingOrganisationRepository;

    @InjectMocks
    private FundingOrganisationService serviceUnderTest;
    private final Integer FUNDER_ID = 1;

    @Test
    void getFundingOrganisationById_Success() {
        final FundingOrganisation organisation = FundingOrganisation.builder()
                .id(FUNDER_ID)
                .name("Company ABC")
                .build();

        when(fundingOrganisationRepository.findById(FUNDER_ID)).thenReturn(Optional.of(organisation));

        FundingOrganisation methodResponse = serviceUnderTest.getFundingOrganisationById(FUNDER_ID);

        verify(fundingOrganisationRepository).findById(FUNDER_ID);
        assertEquals(methodResponse, organisation);
    }

    @Test
    void getFundingOrganisationById_OrgNotFound() {
        when(fundingOrganisationRepository.findById(FUNDER_ID)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.getFundingOrganisationById(FUNDER_ID));
        verify(fundingOrganisationRepository).findById(FUNDER_ID);
        
        assertTrue(result.getMessage().contains("No Funder with ID "+ FUNDER_ID + " was found"));
    }
}