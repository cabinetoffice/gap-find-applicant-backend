package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.model.FundingOrganisation;
import gov.cabinetoffice.gap.applybackend.service.FundingOrganisationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundingOrganisationControllerTest {
    private final Integer FUNDER_ID = 1;
    @Mock
    private FundingOrganisationService fundingOrganisationService;
    @InjectMocks
    private FundingOrganisationController controllerUnderTest;

    @Test
    void getFundingOrganisationById_ReturnsTheCorrectOrganisation() {
        final FundingOrganisation fundingOrganisation = FundingOrganisation.builder()
                .id(FUNDER_ID)
                .name("Company ABC")
                .build();

        when(fundingOrganisationService.getFundingOrganisationById(FUNDER_ID))
                .thenReturn(fundingOrganisation);

        ResponseEntity<FundingOrganisation> response = controllerUnderTest.getFundingOrganisationById(FUNDER_ID);

        verify(fundingOrganisationService).getFundingOrganisationById(FUNDER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), fundingOrganisation);
    }
}
