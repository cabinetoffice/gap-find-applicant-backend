package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import org.junit.jupiter.api.Nested;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

        final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        
        when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(PROFILE_ID, APPLICANT_USER_ID))
                .thenReturn(grantApplicantOrganisationProfile);
        when(modelMapper.map(grantApplicantOrganisationProfile, GetGrantApplicantOrganisationProfileDto.class))
                .thenReturn(getGrantApplicantOrganisationProfileDto);

        ResponseEntity<GetGrantApplicantOrganisationProfileDto> response = controllerUnderTest.getOrganisationById(PROFILE_ID);

        verify(grantApplicantOrganisationProfileService).getProfileByIdAndUserId(PROFILE_ID, APPLICANT_USER_ID);
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

        final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
        
        when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(PROFILE_ID, APPLICANT_USER_ID))
                .thenReturn(grantApplicantOrganisationProfile);
        doNothing().when(modelMapper).map(updateGrantApplicantOrganisationProfileDto, grantApplicantOrganisationProfile);
        ResponseEntity<String> methodResponse = controllerUnderTest.updateOrganisation(PROFILE_ID, updateGrantApplicantOrganisationProfileDto);

        verify(grantApplicantOrganisationProfileService).updateOrganisation(grantApplicantOrganisationProfile);
        assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(methodResponse.getBody())
                .isEqualTo(String.format("Organisation with ID %s has been updated.", grantApplicantOrganisationProfile.getId()));
    }

    @Nested
    class IsOrganisationComplete {
        @Test
        void isOrganisationComplete_ReturnsTrue() {
            final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
            when(grantApplicantOrganisationProfileService.isOrganisationComplete(APPLICANT_USER_ID)).thenReturn(true);

            ResponseEntity<Boolean> response = controllerUnderTest.isOrganisationComplete();

            verify(grantApplicantOrganisationProfileService).isOrganisationComplete(APPLICANT_USER_ID);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody(), true);
        }

        @Test
        void isOrganisationComplete_ReturnsFalse() {
            final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
            when(grantApplicantOrganisationProfileService.isOrganisationComplete(APPLICANT_USER_ID)).thenReturn(false);

            ResponseEntity<Boolean> response = controllerUnderTest.isOrganisationComplete();

            verify(grantApplicantOrganisationProfileService).isOrganisationComplete(APPLICANT_USER_ID);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody(), false);
        }
    }

    @Nested
    class SecurityTests {
        private final String ATTACKER_USER_ID = "attacker-user-id-12345";
        private final long VICTIM_ORG_ID = 999L;

        @Test
        void updateOrganisation_ThrowsForbiddenWhenUserDoesNotOwnOrganisation() {
            final UpdateGrantApplicantOrganisationProfileDto updateDto = UpdateGrantApplicantOrganisationProfileDto.builder()
                    .legalName("Malicious Update")
                    .build();

            final JwtPayload attackerJwt = JwtPayload.builder().sub(ATTACKER_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(attackerJwt);

            when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(VICTIM_ORG_ID, ATTACKER_USER_ID))
                    .thenThrow(new ForbiddenException("You do not have permission to access this organisation"));

            assertThrows(ForbiddenException.class, () -> {
                controllerUnderTest.updateOrganisation(VICTIM_ORG_ID, updateDto);
            });

            // Verify that update was never called (attack was blocked)
            verify(grantApplicantOrganisationProfileService, never()).updateOrganisation(any());
        }

        @Test
        void getOrganisationById_ThrowsForbiddenWhenUserDoesNotOwnOrganisation() {
            final JwtPayload attackerJwt = JwtPayload.builder().sub(ATTACKER_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(attackerJwt);

            when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(VICTIM_ORG_ID, ATTACKER_USER_ID))
                    .thenThrow(new ForbiddenException("You do not have permission to access this organisation"));

            assertThrows(ForbiddenException.class, () -> {
                controllerUnderTest.getOrganisationById(VICTIM_ORG_ID);
            });
        }

        @Test
        void updateOrganisation_SucceedsWhenUserOwnsOrganisation() {
            final UpdateGrantApplicantOrganisationProfileDto updateDto = UpdateGrantApplicantOrganisationProfileDto.builder()
                    .legalName("Legitimate Update")
                    .build();

            final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                    .id(PROFILE_ID)
                    .legalName("Original Name")
                    .build();

            final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

            when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(PROFILE_ID, APPLICANT_USER_ID))
                    .thenReturn(profile);
            doNothing().when(modelMapper).map(updateDto, profile);

            ResponseEntity<String> response = controllerUnderTest.updateOrganisation(PROFILE_ID, updateDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(grantApplicantOrganisationProfileService).updateOrganisation(profile);
        }

        @Test
        void getOrganisationById_SucceedsWhenUserOwnsOrganisation() {
            final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile.builder()
                    .id(PROFILE_ID)
                    .legalName("My Organisation")
                    .build();

            final GetGrantApplicantOrganisationProfileDto profileDto = GetGrantApplicantOrganisationProfileDto.builder()
                    .id(PROFILE_ID)
                    .legalName("My Organisation")
                    .build();

            final JwtPayload jwtPayload = JwtPayload.builder().sub(APPLICANT_USER_ID).build();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);

            when(grantApplicantOrganisationProfileService.getProfileByIdAndUserId(PROFILE_ID, APPLICANT_USER_ID))
                    .thenReturn(profile);
            when(modelMapper.map(profile, GetGrantApplicantOrganisationProfileDto.class))
                    .thenReturn(profileDto);

            ResponseEntity<GetGrantApplicantOrganisationProfileDto> response = controllerUnderTest.getOrganisationById(PROFILE_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(profileDto, response.getBody());
        }
    }
}