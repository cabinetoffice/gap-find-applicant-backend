package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
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
class GrantApplicantOrganisationProfileServiceTest {

    @Mock
    private GrantApplicantOrganisationProfileRepository grantApplicantOrganisationProfileRepository;

    @Mock
    private GrantApplicantService grantApplicantService;

    @InjectMocks
    private GrantApplicantOrganisationProfileService serviceUnderTest;

    private final UUID APPLICANT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");

    @Test
    void getOrganisationById_ReturnsCorrectOrganisation() {
        final long organisationId = 1;
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .id(organisationId)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();
        when(grantApplicantOrganisationProfileRepository.findById(organisationId)).thenReturn(Optional.of(profile));

        GrantApplicantOrganisationProfile methodResponse = serviceUnderTest.getProfileById(organisationId);
        verify(grantApplicantOrganisationProfileRepository).findById(organisationId);
        assertEquals(methodResponse, profile);
    }

    @Test
    void getOrganisationById_OrgNotFound() {
        final long organisationId = 1;
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .id(organisationId)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();
        when(grantApplicantOrganisationProfileRepository.findById(organisationId)).thenReturn(Optional.empty());

        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.getProfileById(organisationId));
        verify(grantApplicantOrganisationProfileRepository).findById(organisationId);
        assertTrue(result.getMessage().contains(String.format("No Organisation Profile with ID %s was found", profile.getId())));
    }

    @Test
    void updateOrganisation_SuccessfullyUpdatesOrg() {
        final long organisationId = 1;
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .id(organisationId)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();

        when(grantApplicantOrganisationProfileRepository.findById(organisationId)).thenReturn(Optional.of(profile));
        when(grantApplicantOrganisationProfileRepository.save(profile)).thenReturn(profile);
        GrantApplicantOrganisationProfile methodResponse = serviceUnderTest.updateOrganisation(profile);

        verify(grantApplicantOrganisationProfileRepository).findById(organisationId);
        verify(grantApplicantOrganisationProfileRepository).save(profile);
        assertEquals(methodResponse, profile);
    }

    @Test
    void updateOrganisation_OrgNotFound() {
        final long organisationId = 1;
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .id(organisationId)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();

        when(grantApplicantOrganisationProfileRepository.findById(organisationId)).thenReturn(Optional.empty());
        Exception result = assertThrows(NotFoundException.class, () -> serviceUnderTest.updateOrganisation(profile));

        verify(grantApplicantOrganisationProfileRepository).findById(organisationId);
        assertTrue(result.getMessage().contains(String.format("No Organisation Profile with ID %s was found", profile.getId())));
    }

    @Test
    void updateOrganisation_SuccessfullyCreateOrg() {
        final GrantApplicant applicant = GrantApplicant.builder()
                .id(1)
                .userId(APPLICANT_ID)
                .build();
        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();

        when(grantApplicantService.getApplicantById(APPLICANT_ID)).thenReturn(applicant);
        when(grantApplicantOrganisationProfileRepository.save(profile)).thenReturn(profile);

        GrantApplicantOrganisationProfile methodResponse = serviceUnderTest.createOrganisation(APPLICANT_ID, profile);

        verify(grantApplicantService).getApplicantById(APPLICANT_ID);
        verify(grantApplicantOrganisationProfileRepository).save(profile);
        assertEquals(methodResponse, profile);
    }
}