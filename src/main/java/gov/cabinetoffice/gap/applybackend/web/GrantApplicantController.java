package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantOrganisationProfileService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-applicant")
public class GrantApplicantController {

    private final GrantApplicantService grantApplicantService;
    private final GrantApplicantOrganisationProfileService grantApplicantOrganisationProfileService;
    private final ModelMapper modelMapper;


    @GetMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Applicant Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantApplicantDto.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Applicant found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantApplicantDto> getGrantApplicantById() {
        JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final String familyName = jwtPayload.getFamilyName();
        final String givenName = jwtPayload.getGivenName();
        final String fullName = String.format("%s %s", givenName, familyName);

        final GrantApplicant applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
        GetGrantApplicantDto applicantDto = modelMapper.map(applicant, GetGrantApplicantDto.class);
        applicantDto.setFullName(WordUtils.capitalize(fullName));
        applicantDto.setEmail(jwtPayload.getEmail());

        return ResponseEntity.ok(applicantDto);
    }

    // TODO refactor this
    @GetMapping("/does-exist")
    public ResponseEntity<Boolean> doesApplicantExist(){
        JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         GrantApplicant applicant = null;
        try {
            applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
        }catch (NotFoundException ignored){
        }
        return ResponseEntity.ok(applicant != null);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createApplicant(){
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant applicant = GrantApplicant.builder()
                .userId(jwtPayload.getSub())
                .build();

        grantApplicantService.saveApplicant(applicant);

        final GrantApplicantOrganisationProfile profile = GrantApplicantOrganisationProfile
                .builder()
                .build();
        grantApplicantOrganisationProfileService.createOrganisation(jwtPayload.getSub(), profile);

        return ResponseEntity.ok("User has been created");
    }
}
