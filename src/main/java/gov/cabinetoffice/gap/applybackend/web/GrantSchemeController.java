package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.dto.api.*;
import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertStatus;
import gov.cabinetoffice.gap.applybackend.mapper.GrantSchemeMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicationService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/grant-schemes")
public class GrantSchemeController {

    private final GrantSchemeService grantSchemeService;
    private final GrantSchemeMapper grantSchemeMapper;
    private final GrantAdvertService grantAdvertService;
    private final GrantApplicationService grantApplicationService;
    private final EnvironmentProperties environmentProperties;

    @GetMapping("/{grantSchemeId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Scheme Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetGrantSchemeWithApplicationAndAdverts.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Scheme found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantSchemeWithApplicationAndAdverts> getGrantSchemeById(@PathVariable Integer grantSchemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final GrantScheme grantScheme = grantSchemeService.getSchemeByIdWithApplicationAndAdverts(grantSchemeId);
        final GetGrantSchemeDto grantSchemeDto = new GetGrantSchemeDto(grantScheme);
        final GetGrantApplicationDto grantApplicationDto = grantSchemeMapper.grantSchemeToGetGrantApplicationDto(grantScheme);
        final List<GetGrantAdvertDto> grantAdvertDtos = grantScheme.getGrantAdverts().stream()
                .filter(grantAdvert -> grantAdvert.getStatus().equals(GrantAdvertStatus.PUBLISHED))
                .map(grantAdvert -> grantAdvertService.grantAdvertToDto(grantAdvert, jwtPayload.getSub(), grantSchemeId))
                .toList();

        final GetGrantSchemeWithApplicationAndAdverts getGrantSchemeWithApplicationAndAdverts = GetGrantSchemeWithApplicationAndAdverts.builder()
                .grantScheme(grantSchemeDto)
                .grantApplication(grantApplicationDto)
                .grantAdverts(grantAdvertDtos)
                .build();
        return ResponseEntity.ok(getGrantSchemeWithApplicationAndAdverts);
    }
    @GetMapping("/{grantSchemeId}/hasInternalApplication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check if grant scheme is for internal or external application",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Scheme found",
                    content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<SchemeMandatoryQuestionApplicationFormInfosDto> schemeHasInternalApplication(@PathVariable Integer grantSchemeId){
        log.debug("Checking if scheme with id {} has internal application form and if it is published",grantSchemeId);

        final GrantScheme grantScheme = grantSchemeService.getSchemeById(grantSchemeId);
        log.debug("Scheme with id {} found. Getting associated advert", grantSchemeId);

        log.debug("Getting Advert associated to scheme with id {}", grantSchemeId);
        final GrantAdvert advert = grantAdvertService.getAdvertBySchemeId(grantSchemeId.toString());
        log.debug("Advert with id {} found for scheme with id {}", advert.getId(), grantSchemeId);

        final String webpageUrl = grantAdvertService.getExternalSubmissionUrl(advert);

        final boolean webPageUrlIsForInternalApplications = webpageUrl.contains(environmentProperties.getFrontEndUri());
        log.debug("Advert is pointing to an internal application form : {}", webPageUrlIsForInternalApplications);

        final SchemeMandatoryQuestionApplicationFormInfosDto dto = SchemeMandatoryQuestionApplicationFormInfosDto.builder()
                .hasInternalApplication(webPageUrlIsForInternalApplications)
                .hasPublishedInternalApplication(false)
                .build();

        if (webPageUrlIsForInternalApplications) {
            final boolean hasPublishedApplication = grantApplicationService.doesSchemeHaveAPublishedApplication(grantScheme);
            log.debug("Application form associated to advert with Id {} has PUBLISHED status: {}", advert.getId(), hasPublishedApplication);
            dto.setHasPublishedInternalApplication(hasPublishedApplication);
        }

        log.debug("Scheme with ID {} is for internal application: {} and has a published application form : {}",
                grantSchemeId, dto.isHasInternalApplication(), dto.isHasPublishedInternalApplication());

        return ResponseEntity.ok(dto);
    }

}
