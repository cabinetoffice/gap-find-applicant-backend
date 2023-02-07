package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetFundingOrganisationDto;
import gov.cabinetoffice.gap.applybackend.model.FundingOrganisation;
import gov.cabinetoffice.gap.applybackend.service.FundingOrganisationService;
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
@RequestMapping("/funding-organisations")
public class FundingOrganisationController {

    private final FundingOrganisationService fundingOrganisationService;

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funding organisation Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetFundingOrganisationDto.class))),
            @ApiResponse(responseCode = "404", description = "No funding organisation found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<FundingOrganisation> getFundingOrganisationById(@PathVariable Integer id) {
        FundingOrganisation organisation = fundingOrganisationService.getFundingOrganisationById(id);
        return ResponseEntity.ok(organisation);
    }
}
