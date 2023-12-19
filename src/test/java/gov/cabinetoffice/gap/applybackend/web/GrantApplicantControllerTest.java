package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantApplicantControllerTest {
    private final long APPLICANT_ID = 1;
    private final long PROFILE_ID = 1;
    private final String APPLICANT_USER_ID = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private GrantApplicantService grantApplicantService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GrantApplicantOrganisationProfileService grantApplicantOrganisationProfileService;
    @InjectMocks
    private GrantApplicantController controllerUnderTest;

    @Test
    void getApplicantById_ReturnsTheCorrectApplicant() {
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
        final GetGrantApplicantDto getGrantApplicantDto = GetGrantApplicantDto.builder()
                .id(APPLICANT_ID)
                .fullName("John Smith")
                .organisation(getGrantApplicantOrganisationProfileDto)
                .build();

        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
                .id(PROFILE_ID)
                .legalName("Postman Test Org")
                .legalName("Postman Test Org")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .build();
        final GrantApplicant grantApplicant = GrantApplicant.builder()
                .id(APPLICANT_ID)
                .userId(APPLICANT_USER_ID)
                .organisationProfile(grantApplicantOrganisationProfile)
                .build();


        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID)
                .givenName("John")
                .familyName("Smith")
                .build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

        when(grantApplicantService.getApplicantById(APPLICANT_USER_ID))
                .thenReturn(grantApplicant);
        when(modelMapper.map(grantApplicant, GetGrantApplicantDto.class))
                .thenReturn(getGrantApplicantDto);


        ResponseEntity<GetGrantApplicantDto> response = controllerUnderTest.getGrantApplicantById();

        verify(securityContext, times(2)).getAuthentication();
        verify(grantApplicantService).getApplicantById(APPLICANT_USER_ID);
        verify(modelMapper).map(grantApplicant, GetGrantApplicantDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), getGrantApplicantDto);
    }

    @Test
    void doesApplicantExist_ReturnTrue() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID)
                .build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        GrantApplicant grantApplicant = GrantApplicant.builder().userId(APPLICANT_USER_ID).build();
        when(grantApplicantService.getApplicantById(APPLICANT_USER_ID)).thenReturn(grantApplicant);
        ResponseEntity<Boolean> response = controllerUnderTest.doesApplicantExist();
        assertEquals(true, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void doesApplicantExist_ReturnFalse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID)
                .build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(grantApplicantService.getApplicantById(APPLICANT_USER_ID)).thenThrow(new NotFoundException(""));

        ResponseEntity<Boolean> response = controllerUnderTest.doesApplicantExist();
        assertEquals(false, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createApplicant_CreatesApplicant() {
        final JwtPayload jwtPayload = JwtPayload.builder()
                .sub(APPLICANT_USER_ID)
                .givenName("John")
                .familyName("Smith")
                .build();

        final GrantApplicant applicant = GrantApplicant.builder()
                .userId(APPLICANT_USER_ID)
                .build();

        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                .build();

        SecurityContextHolder.setContext(securityContext);

        final ArgumentCaptor<GrantApplicant> applicantCaptor = ArgumentCaptor.forClass(GrantApplicant.class);
        final ArgumentCaptor<GrantApplicantOrganisationProfile> profileCaptor = ArgumentCaptor.forClass(GrantApplicantOrganisationProfile.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        when(grantApplicantService.saveApplicant(any()))
                .thenReturn(applicant);
        when(grantApplicantOrganisationProfileService.createOrganisation(eq(APPLICANT_USER_ID), any()))
                .thenReturn(profile);

        final ResponseEntity<String> methodResponse = controllerUnderTest.createApplicant();

        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody()).isEqualTo("User has been created");

        verify(grantApplicantService).saveApplicant(applicantCaptor.capture());
        verify(grantApplicantOrganisationProfileService).createOrganisation(eq(APPLICANT_USER_ID), profileCaptor.capture());

        final GrantApplicant createdApplicant = applicantCaptor.getValue();
        assertThat(createdApplicant.getUserId()).isEqualTo(applicant.getUserId());

        final GrantApplicantOrganisationProfile createdProfile = profileCaptor.getValue();
        assertThat(createdProfile).isEqualTo(profile);
    }
}
