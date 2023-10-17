package gov.cabinetoffice.gap.applybackend.web;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicantDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        GrantApplication grantApplication = grantApplicationService.getGrantApplicationByGrantScheme(schemeId);
        return ResponseEntity.ok(grantApplication.getApplicationStatus().toString());
    }
}
