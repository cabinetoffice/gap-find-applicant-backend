package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantDto;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-application")
public class GrantApplicationController {

    private final GrantApplicationService grantApplicationService;

    @GetMapping("/{schemeId}/status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Application Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantApplicantDto.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Application found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> getApplicationStatusFromSchemeId(
            @PathVariable("schemeId") Integer schemeId
    ) {
        try {
            GrantApplication grantApplication = grantApplicationService.getGrantApplicationByGrantScheme(schemeId);
            return ResponseEntity.ok(grantApplication.getApplicationStatus().toString());
        } catch (Exception e) {
            // From an applicants POV - if there is no application form then it is an external application.
            return ResponseEntity.ok("EXTERNAL_APPLICATION");
        }
    }

    @GetMapping("/{applicationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Application Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantApplicantDto.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Application found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GrantApplication> getApplicationByApplicationId(
            @PathVariable("applicationId") int applicationId
    ) {
        GrantApplication grantApplication = grantApplicationService.getGrantApplicationById(applicationId);
        return ResponseEntity.ok(grantApplication);
    }
}
