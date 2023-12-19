package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.GetContentfulAdvertExistsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantAdvertDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantMandatoryQuestionMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-adverts")
public class GrantAdvertController {

    private final GrantAdvertService grantAdvertService;
    private final GrantMandatoryQuestionService grantMandatoryQuestionService;
    private final GrantApplicantService grantApplicantService;
    private final GrantMandatoryQuestionMapper mapper;

    @GetMapping
    @Operation(summary = "Get the grant advert with the given contentful slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully got grant advert with contentful slug provided"),
            @ApiResponse(responseCode = "400", description = "Required path variable not provided in expected format",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Unable to find grant advert with contentful slug provided",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GetGrantAdvertDto> generateGetGrantAdvertDtoFromAdvertSlug(@RequestParam @NotBlank String contentfulSlug) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GetGrantAdvertDto grantAdvertDto = GetGrantAdvertDto.builder()
                .isAdvertInDatabase(false)
                .build();

        try {
            final GrantApplicant grantApplicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
            final GrantAdvert advert = grantAdvertService.getAdvertByContentfulSlug(contentfulSlug);

            GetGrantMandatoryQuestionDto mandatoryQuestionsDto = null;

            if (grantMandatoryQuestionService.existsBySchemeIdAndApplicantId(advert.getScheme().getId(), grantApplicant.getId())) {
                final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getMandatoryQuestionBySchemeId(advert.getScheme().getId(), jwtPayload.getSub());
                mandatoryQuestionsDto = mapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
            }

            grantAdvertDto = grantAdvertService.generateGetGrantAdvertDto(advert, mandatoryQuestionsDto);

        } catch (NotFoundException e) {
            log.info("Advert with slug " + contentfulSlug + " not found");
        }

        return ResponseEntity.ok(grantAdvertDto);
    }

    @GetMapping("/scheme/{schemeId}")
    @Operation(summary = "Get the grant advert with the given scheme Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully got grant advert with schemeId provided"),
            @ApiResponse(responseCode = "400", description = "Required path variable not provided in expected format",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Unable to find grant advert with scheme Id provided",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GetGrantAdvertDto> generateGetGrantAdvertDtoFromSchemeId(@PathVariable final String schemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant grantApplicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
        final GrantAdvert advert = grantAdvertService.getAdvertBySchemeId(schemeId);

        GetGrantMandatoryQuestionDto mandatoryQuestionsDto = null;

        if (grantMandatoryQuestionService.existsBySchemeIdAndApplicantId(Integer.parseInt(schemeId), grantApplicant.getId())) {
            final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService
                    .getMandatoryQuestionBySchemeId(Integer.parseInt(schemeId), jwtPayload.getSub());
            mandatoryQuestionsDto = mapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
        }

        return ResponseEntity.ok(grantAdvertService.generateGetGrantAdvertDto(advert, mandatoryQuestionsDto));
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
