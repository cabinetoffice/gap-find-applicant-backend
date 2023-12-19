package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantApplicantOrganisationProfileDto;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-applicant-organisation-profile")
public class GrantApplicantOrganisationProfileController {
    private final ModelMapper modelMapper;
    private final GrantApplicantOrganisationProfileService grantApplicantOrganisationProfileService;

    @GetMapping("/{organisationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organisation found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantApplicantOrganisationProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "No Organisation found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantApplicantOrganisationProfileDto> getOrganisationById(@PathVariable long organisationId) {
        GrantApplicantOrganisationProfile profile = grantApplicantOrganisationProfileService.getProfileById(organisationId);
        GetGrantApplicantOrganisationProfileDto organisationDto = modelMapper.map(profile, GetGrantApplicantOrganisationProfileDto.class);
        return ResponseEntity.ok(organisationDto);
    }

    @GetMapping("/isComplete")
    public ResponseEntity<Boolean> isOrganisationComplete() {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(grantApplicantOrganisationProfileService.isOrganisationComplete(jwtPayload.getSub()));
    }

    @PatchMapping("/{organisationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organisation updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "No Organisation found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> updateOrganisation(@PathVariable long organisationId,
                                                     @RequestBody @Valid UpdateGrantApplicantOrganisationProfileDto organisation) {
        GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = grantApplicantOrganisationProfileService.getProfileById(organisationId);
        modelMapper.map(organisation, grantApplicantOrganisationProfile);
        grantApplicantOrganisationProfile.setId(organisationId);
        grantApplicantOrganisationProfileService.updateOrganisation(grantApplicantOrganisationProfile);
        return ResponseEntity.ok(String.format("Organisation with ID %s has been updated.", organisationId));
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organisation created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<String> createOrganisation(@RequestBody CreateGrantApplicantOrganisationProfileDto organisation) {
        JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = modelMapper.map(organisation, GrantApplicantOrganisationProfile.class);
        grantApplicantOrganisationProfileService.createOrganisation(jwtPayload.getSub(), grantApplicantOrganisationProfile);
        return new ResponseEntity<>(String.format("An organisation with the id %s has been created", grantApplicantOrganisationProfile.getId()), HttpStatus.CREATED);
    }
}
