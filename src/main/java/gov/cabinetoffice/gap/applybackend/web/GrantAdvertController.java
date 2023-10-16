package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.GetContentfulAdvertExistsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Slf4j
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
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GetGrandAdvertDto> generateGetGrantAdvertDtoFromAdvertSlug(@RequestParam @NotBlank String contentfulSlug) {

        GetGrandAdvertDto grantAdvert = GetGrandAdvertDto.builder()
                .isAdvertInDatabase(false)
                .build();

        try {
            grantAdvert = grantAdvertService.getAdvertByContentfulSlug(contentfulSlug);
        } catch (NotFoundException e) {
            log.info("Advert with slug " + contentfulSlug + " not found");
        }

        return ResponseEntity.ok(grantAdvert);
    }


    @GetMapping("{advertSlug}/exists-in-contentful")
    @Operation(summary = "Check whether a grant advert exists in Contentful with the given slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got grant advert with contentful slug provided"),
            @ApiResponse(responseCode = "400", description = "Required path variable not provided in expected format",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GetContentfulAdvertExistsDto> advertExistsInContentful(@PathVariable final String advertSlug) {
        final boolean advertExists = grantAdvertService.advertExistsInContentful(advertSlug);

        return ResponseEntity.ok(
                GetContentfulAdvertExistsDto
                .builder()
                .isAdvertInContentful(advertExists)
                .build()
        );
    }
}
