package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-mandatory-questions")
@Slf4j
public class GrantMandatoryQuestionsController {
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    private final GrantMandatoryQuestionService grantMandatoryQuestionService;
    private final GrantApplicantService grantApplicantService;
    private final GrantSchemeService grantSchemeService;
    //change to mapstruct
    private final ModelMapper modelMapper;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UUID> createMandatoryQuestion(@RequestParam final Integer schemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
        final GrantScheme scheme = grantSchemeService.getSchemeById(schemeId);

        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.createMandatoryQuestion(scheme, applicant);
        log.info("Mandatory question with ID {} has been created.", grantMandatoryQuestions.getId());
        return ResponseEntity.ok(grantMandatoryQuestions.getId());
    }

    @GetMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> getGrantMandatoryQuestionsById(@PathVariable final UUID mandatoryQuestionId, @RequestParam final String url) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());
        log.info("Mandatory question with ID {} has been grabbed.", grantMandatoryQuestions.getId());

        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionDto = modelMapper.map(grantMandatoryQuestions, GetGrantMandatoryQuestionDto.class);
        final boolean isPageAlreadyAnswered = grantMandatoryQuestionService.isPageAlreadyAnswered(url, getGrantMandatoryQuestionDto);
        final String nextNotAnsweredPage = grantMandatoryQuestionService.generateNextPageUrl(grantMandatoryQuestions);

        getGrantMandatoryQuestionDto.setNextNotAnsweredPage(nextNotAnsweredPage);
        getGrantMandatoryQuestionDto.setPageAlreadyAnswered(isPageAlreadyAnswered);
        //TODO: set mapstruct to do this
        if (grantMandatoryQuestions.getFundingLocation() != null) {
            List<String> locations = new ArrayList<>();
            for (int i = 0; i < grantMandatoryQuestions.getFundingLocation().length; i++) {
                String location = grantMandatoryQuestions.getFundingLocation()[i].getName();
                locations.add(location);
            }
            getGrantMandatoryQuestionDto.setFundingLocation(locations);
        }
        return ResponseEntity.ok(getGrantMandatoryQuestionDto);
    }

    @PatchMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> updateMandatoryQuestion(@PathVariable final UUID mandatoryQuestionId,
                                                          @RequestBody @Valid final UpdateGrantMandatoryQuestionDto mandatoryQuestionDto) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());

        mapDtoToEntity(mandatoryQuestionDto, grantMandatoryQuestions);
        grantMandatoryQuestionService.updateMandatoryQuestion(grantMandatoryQuestions);
        log.info("Mandatory question with ID {} has been updated.", mandatoryQuestionId);

        return ResponseEntity.ok(grantMandatoryQuestionService.generateNextPageUrl(grantMandatoryQuestions));
    }

    //TODO: change it to mapstruct when turning the validation on
    protected void mapDtoToEntity(UpdateGrantMandatoryQuestionDto mandatoryQuestionDto, GrantMandatoryQuestions grantMandatoryQuestions) {
        if (mandatoryQuestionDto.getName() != null) {
            grantMandatoryQuestions.setName(mandatoryQuestionDto.getName());
        }
        if (mandatoryQuestionDto.getAddressLine1() != null) {
            grantMandatoryQuestions.setAddressLine1(mandatoryQuestionDto.getAddressLine1());
        }
        if (mandatoryQuestionDto.getAddressLine2() != null) {
            grantMandatoryQuestions.setAddressLine2(mandatoryQuestionDto.getAddressLine2());
        }
        if (mandatoryQuestionDto.getCity() != null) {
            grantMandatoryQuestions.setCity(mandatoryQuestionDto.getCity());
        }
        if (mandatoryQuestionDto.getCounty() != null) {
            grantMandatoryQuestions.setCounty(mandatoryQuestionDto.getCounty());
        }
        if (mandatoryQuestionDto.getPostcode() != null) {
            grantMandatoryQuestions.setPostcode(mandatoryQuestionDto.getPostcode());
        }
        if (mandatoryQuestionDto.getOrgType() != null) {
            grantMandatoryQuestions.setOrgType(GrantMandatoryQuestionOrgType.valueOfName(mandatoryQuestionDto.getOrgType()));
        }
        if (mandatoryQuestionDto.getCompaniesHouseNumber() != null) {
            grantMandatoryQuestions.setCompaniesHouseNumber(mandatoryQuestionDto.getCompaniesHouseNumber());
        }
        if (mandatoryQuestionDto.getCharityCommissionNumber() != null) {
            grantMandatoryQuestions.setCharityCommissionNumber(mandatoryQuestionDto.getCharityCommissionNumber());
        }
        if (mandatoryQuestionDto.getFundingAmount() != null) {
            grantMandatoryQuestions.setFundingAmount(new BigDecimal(mandatoryQuestionDto.getFundingAmount()));
        }
        if (mandatoryQuestionDto.getFundingLocation() != null) {
            List<String> locations = mandatoryQuestionDto.getFundingLocation();
            GrantMandatoryQuestionFundingLocation[] grantMandatoryQuestionFundingLocations = new GrantMandatoryQuestionFundingLocation[locations.size()];
            for (int i = 0; i < locations.size(); i++) {
                grantMandatoryQuestionFundingLocations[i] = GrantMandatoryQuestionFundingLocation.valueOfName(locations.get(i));
            }
            grantMandatoryQuestions.setFundingLocation(grantMandatoryQuestionFundingLocations);
        }
        if (grantMandatoryQuestions.getStatus().equals(GrantMandatoryQuestionStatus.NOT_STARTED)) {
            grantMandatoryQuestions.setStatus(GrantMandatoryQuestionStatus.IN_PROGRESS);
        }
    }

}
