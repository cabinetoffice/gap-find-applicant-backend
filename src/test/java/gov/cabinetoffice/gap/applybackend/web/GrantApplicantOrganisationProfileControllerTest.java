package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantApplicantOrganisationProfileControllerTest {

    private final long PROFILE_ID = 1;
    private final String APPLICANT_USER_ID = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
    @Mock
    private GrantApplicantOrganisationProfileService grantApplicantOrganisationProfileService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private GrantApplicantOrganisationProfileController controllerUnderTest;

    @Test
    void getOrganisationById_ReturnsTheCorrectOrganisation() {
        final GetGrantApplicantOrganisationProfileDto getGrantApplicantOrganisationProfileDto = GetGrantApplicantOrganisationProfileDto.builder()
                .id(PROFILE_ID)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();
        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
                .id(PROFILE_ID)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();

        when(grantApplicantOrganisationProfileService.getProfileById(PROFILE_ID))
                .thenReturn(grantApplicantOrganisationProfile);
        when(modelMapper.map(grantApplicantOrganisationProfile, GetGrantApplicantOrganisationProfileDto.class))
                .thenReturn(getGrantApplicantOrganisationProfileDto);

        ResponseEntity<GetGrantApplicantOrganisationProfileDto> response = controllerUnderTest.getOrganisationById(PROFILE_ID);

        verify(grantApplicantOrganisationProfileService).getProfileById(PROFILE_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), getGrantApplicantOrganisationProfileDto);
    }

    @Test
    void createOrganisation_Success() {
        final CreateGrantApplicantOrganisationProfileDto createGrantApplicantOrganisationProfileDto = CreateGrantApplicantOrganisationProfileDto.builder()
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();
        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
                .id(PROFILE_ID)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(modelMapper.map(createGrantApplicantOrganisationProfileDto, GrantApplicantOrganisationProfile.class))
                .thenReturn(grantApplicantOrganisationProfile);
        when(grantApplicantOrganisationProfileService.createOrganisation(APPLICANT_USER_ID, grantApplicantOrganisationProfile))
                .thenReturn(grantApplicantOrganisationProfile);

        ResponseEntity<String> response = controllerUnderTest
                .createOrganisation(createGrantApplicantOrganisationProfileDto);

        verify(grantApplicantOrganisationProfileService).createOrganisation(jwtPayload.getSub(), grantApplicantOrganisationProfile);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getBody(), String.format("An organisation with the id %s has been created",
                grantApplicantOrganisationProfile.getId()));
    }

    @Test
    void updateOrganisation_UpdatesTheCorrectOrg() {
        final UpdateGrantApplicantOrganisationProfileDto updateGrantApplicantOrganisationProfileDto = UpdateGrantApplicantOrganisationProfileDto.builder()
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .build();

        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
                .id(PROFILE_ID)
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .build();

        when(grantApplicantOrganisationProfileService.getProfileById(PROFILE_ID))
                .thenReturn(grantApplicantOrganisationProfile);
        doNothing().when(modelMapper).map(updateGrantApplicantOrganisationProfileDto, grantApplicantOrganisationProfile);
        ResponseEntity<String> methodResponse = controllerUnderTest.updateOrganisation(PROFILE_ID, updateGrantApplicantOrganisationProfileDto);

        verify(grantApplicantOrganisationProfileService).updateOrganisation(grantApplicantOrganisationProfile);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody())
                .isEqualTo(String.format("Organisation with ID %s has been updated.", grantApplicantOrganisationProfile.getId()));
    }
}