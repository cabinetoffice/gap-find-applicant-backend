package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    private final String APPLICANT_ID = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";

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

    @Nested
    class IsOrganisationComplete {
        @ParameterizedTest
        @CsvSource({
                "INDIVIDUAL,Org name,9 George Square,Glasgow,G2 1QQ,,",
                "NON_LIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,,",
                "LIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "UNLIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "REGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "UNREGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "OTHER,Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
        })
        void returnsTrue(
                final GrantApplicantOrganisationType type,
                final String name,
                final String addressLine1,
                final String town,
                final String postcode,
                final String companiesHouseNumber,
                final String charityCommissionNumber
        ) {
            final GrantApplicant applicant = GrantApplicant.builder()
                    .id(1)
                    .userId(APPLICANT_ID)
                    .build();
            final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                    .charityCommissionNumber(charityCommissionNumber)
                    .companiesHouseNumber(companiesHouseNumber)
                    .addressLine1(addressLine1)
                    .town(town)
                    .postcode(postcode)
                    .type(type)
                    .legalName(name)
                    .build();
            applicant.setOrganisationProfile(profile);

            when(grantApplicantService.getApplicantById(APPLICANT_ID)).thenReturn(applicant);

            Boolean methodResponse = serviceUnderTest.isOrganisationComplete(APPLICANT_ID);

            verify(grantApplicantService).getApplicantById(APPLICANT_ID);
            assertTrue(methodResponse);
        }

        @ParameterizedTest
        @CsvSource({
                ",Org name,9 George Square,Glasgow,G2 1QQ,45,000010",
                "INDIVIDUAL,Org name,9 George Square,Glasgow,,45,000010",
                "INDIVIDUAL,Org name,9 George Square,,G2 1QQ,45,000010",
                "INDIVIDUAL,Org name,,Glasgow,G2 1QQ,45,000010",
                "INDIVIDUAL,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "NON_LIMITED_COMPANY,Org name,9 George Square,Glasgow,,45,000010",
                "NON_LIMITED_COMPANY,Org name,9 George Square,,G2 1QQ,45,000010",
                "NON_LIMITED_COMPANY,Org name,,Glasgow,G2 1QQ,45,000010",
                "NON_LIMITED_COMPANY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "LIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "LIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "LIMITED_COMPANY,Org name,9 George Square,Glasgow,,45,000010",
                "LIMITED_COMPANY,Org name,9 George Square,,G2 1QQ,45,000010",
                "LIMITED_COMPANY,Org name,,Glasgow,G2 1QQ,45,000010",
                "LIMITED_COMPANY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "UNLIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "UNLIMITED_COMPANY,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "UNLIMITED_COMPANY,Org name,9 George Square,Glasgow,,45,000010",
                "UNLIMITED_COMPANY,Org name,9 George Square,,G2 1QQ,45,000010",
                "UNLIMITED_COMPANY,Org name,,Glasgow,G2 1QQ,45,000010",
                "UNLIMITED_COMPANY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "REGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "REGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "REGISTERED_CHARITY,Org name,9 George Square,Glasgow,,45,000010",
                "REGISTERED_CHARITY,Org name,9 George Square,,G2 1QQ,45,000010",
                "REGISTERED_CHARITY,Org name,,Glasgow,G2 1QQ,45,000010",
                "REGISTERED_CHARITY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "UNREGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "UNREGISTERED_CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "UNREGISTERED_CHARITY,Org name,9 George Square,Glasgow,,45,000010",
                "UNREGISTERED_CHARITY,Org name,9 George Square,,G2 1QQ,45,000010",
                "UNREGISTERED_CHARITY,Org name,,Glasgow,G2 1QQ,45,000010",
                "UNREGISTERED_CHARITY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "CHARITY,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "CHARITY,Org name,9 George Square,Glasgow,,45,000010",
                "CHARITY,Org name,9 George Square,,G2 1QQ,45,000010",
                "CHARITY,Org name,,Glasgow,G2 1QQ,45,000010",
                "CHARITY,,9 George Square,Glasgow,G2 1QQ,45,000010",
                "OTHER,Org name,9 George Square,Glasgow,G2 1QQ,45,",
                "OTHER,Org name,9 George Square,Glasgow,G2 1QQ,,000010",
                "OTHER,Org name,9 George Square,Glasgow,,45,000010",
                "OTHER,Org name,9 George Square,,G2 1QQ,45,000010",
                "OTHER,Org name,,Glasgow,G2 1QQ,45,000010",
                "OTHER,,9 George Square,Glasgow,G2 1QQ,45,000010",
        })
        void returnsFalse(
                final GrantApplicantOrganisationType type,
                final String name,
                final String addressLine1,
                final String town,
                final String postcode,
                final String companiesHouseNumber,
                final String charityCommissionNumber
        ) {
            final GrantApplicant applicant = GrantApplicant.builder()
                    .id(1)
                    .userId(APPLICANT_ID)
                    .build();
            final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                    .charityCommissionNumber(charityCommissionNumber)
                    .companiesHouseNumber(companiesHouseNumber)
                    .addressLine1(addressLine1)
                    .town(town)
                    .postcode(postcode)
                    .type(type)
                    .legalName(name)
                    .build();
            applicant.setOrganisationProfile(profile);

            when(grantApplicantService.getApplicantById(APPLICANT_ID)).thenReturn(applicant);

            Boolean methodResponse = serviceUnderTest.isOrganisationComplete(APPLICANT_ID);

            verify(grantApplicantService).getApplicantById(APPLICANT_ID);
            assertFalse(methodResponse);
        }

    }
}