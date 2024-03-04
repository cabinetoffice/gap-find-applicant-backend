package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.*;
import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertStatus;
import gov.cabinetoffice.gap.applybackend.mapper.GrantSchemeMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-schemes")
public class GrantSchemeController {

    private final GrantSchemeService grantSchemeService;
    private final GrantSchemeMapper grantSchemeMapper;
    private final GrantAdvertService grantAdvertService;

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
                .map(grantAdvert ->
                        grantAdvert.getStatus().equals(GrantAdvertStatus.PUBLISHED) ?
                        grantAdvertService.grantAdvertToDto(grantAdvert, jwtPayload.getSub(), grantSchemeId) : null)
                .toList();

        final GetGrantSchemeWithApplicationAndAdverts getGrantSchemeWithApplicationAndAdverts = GetGrantSchemeWithApplicationAndAdverts.builder()
                .grantScheme(grantSchemeDto)
                .grantApplication(grantApplicationDto)
                .grantAdverts(grantAdvertDtos)
                .build();
        return ResponseEntity.ok(getGrantSchemeWithApplicationAndAdverts);
    }
}
