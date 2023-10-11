package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-adverts")
public class GrantAdvertController {

    private final GrantAdvertService grantAdvertService;
    @GetMapping
    @Operation(summary = "Get the grant advert with the given contentful slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully got grant advert with contentful slug provided"),
            @ApiResponse(responseCode = "400", description = "Required path variable not provided in expected format",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Unable to find grant advert with contentful slug provided",
                    content = @Content(mediaType = "application/json")) })
    public ResponseEntity<GetGrandAdvertDto> generateGetGrantAdvertDtoFromAdvertSlug(@RequestParam @NotBlank String contentfulSlug) {

        final GetGrandAdvertDto grantAdvert = grantAdvertService.getAdvertByContentfulSlug(contentfulSlug);

        return ResponseEntity.ok(grantAdvert);
    }
}
