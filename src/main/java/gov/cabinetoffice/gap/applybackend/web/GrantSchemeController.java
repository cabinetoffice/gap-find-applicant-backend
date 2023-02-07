package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeDto;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
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
@RequestMapping("/grant-schemes")
public class GrantSchemeController {

    private final GrantSchemeService grantSchemeService;

    @GetMapping("/{grantSchemeId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Scheme Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantSchemeDto.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Scheme found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GrantScheme> getGrantSchemeById(@PathVariable Integer grantSchemeId) {
        GrantScheme scheme = grantSchemeService.getSchemeById(grantSchemeId);
        return ResponseEntity.ok(scheme);
    }
}
