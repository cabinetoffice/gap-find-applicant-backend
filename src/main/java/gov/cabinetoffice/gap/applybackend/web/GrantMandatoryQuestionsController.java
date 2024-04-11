package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.config.properties.EnvironmentProperties;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import gov.cabinetoffice.gap.applybackend.mapper.GrantMandatoryQuestionMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import gov.cabinetoffice.gap.applybackend.service.GrantSchemeService;
import gov.cabinetoffice.gap.applybackend.service.SubmissionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.UUID;

import static gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType.INDIVIDUAL;
import static gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType.LOCAL_AUTHORITY;
import static gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType.OTHER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-mandatory-questions")
@Slf4j
public class GrantMandatoryQuestionsController {

    private final GrantMandatoryQuestionService grantMandatoryQuestionService;
    private final GrantApplicantService grantApplicantService;
    private final GrantSchemeService grantSchemeService;
    private final GrantMandatoryQuestionMapper grantMandatoryQuestionMapper;
    private final GrantAdvertService grantAdvertService;
    private final SubmissionService submissionService;
    private final EnvironmentProperties environmentProperties;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> createMandatoryQuestion(@RequestParam final Integer schemeId) {
        log.info("Creating mandatory question for scheme id {}", schemeId);
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());

        log.info("Getting scheme with id {}", schemeId);
        final GrantScheme scheme = grantSchemeService.getSchemeById(schemeId);
        log.info("Scheme with id {} found", schemeId);

        log.info("Getting Advert associated to scheme with id {}", schemeId);
        final GrantAdvert advert = grantAdvertService.getAdvertBySchemeId(schemeId.toString());
        log.info("Advert with id {} found for scheme with id {}", advert.getId(), schemeId);

        final String webpageUrl = grantAdvertService.getExternalSubmissionUrl(advert);

        log.info("Checking that the advert has link to internal or external application");
        final boolean webPageUrlIsForInternalApplications = webpageUrl.contains(environmentProperties.getFrontEndUri());
        log.info("Advert is pointing to an internal application form : {}", webPageUrlIsForInternalApplications);

        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.createMandatoryQuestion(scheme, applicant, webPageUrlIsForInternalApplications);
        log.info("Mandatory question with ID {} has been created.", grantMandatoryQuestions.getId());

        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionDto = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);

        return ResponseEntity.ok(getGrantMandatoryQuestionDto);
    }


    @GetMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> getGrantMandatoryQuestionsById(@PathVariable final UUID mandatoryQuestionId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());
        log.info("Mandatory question with ID {} has been retrieved.", grantMandatoryQuestions.getId());

        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionDto = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
        return ResponseEntity.ok(getGrantMandatoryQuestionDto);
    }

    @GetMapping("/get-by-submission/{submissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> getGrantMandatoryQuestionsBySubmissionId(@PathVariable final UUID submissionId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionBySubmissionIdAndApplicantSub(submissionId, jwtPayload.getSub());
        log.info("Mandatory question with ID {} has been retrieved.", grantMandatoryQuestions.getId());

        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionBySubmissionDto = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
        return ResponseEntity.ok(getGrantMandatoryQuestionBySubmissionDto);
    }

    @GetMapping("/scheme/{schemeId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> getGrantMandatoryQuestionsBySchemeId(@PathVariable final Integer schemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getMandatoryQuestionBySchemeId(schemeId, jwtPayload.getSub());

        log.info("Mandatory question with ID {} has been retrieved", grantMandatoryQuestions.getId());

        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionBySubmissionDto = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
        return ResponseEntity.ok(getGrantMandatoryQuestionBySubmissionDto);
    }

    @GetMapping("/scheme/{schemeId}/exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Boolean> existsBySchemeIdAndApplicantId(@PathVariable final Integer schemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());

        return ResponseEntity.ok(grantMandatoryQuestionService.mandatoryQuestionExistsBySchemeIdAndApplicantId(schemeId, applicant.getId()));
    }


    @PatchMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> updateMandatoryQuestion(@PathVariable final UUID mandatoryQuestionId,
                                                          @RequestBody @Valid final UpdateGrantMandatoryQuestionDto mandatoryQuestionDto, @RequestParam final String url) {

        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant applicant = grantApplicantService.getApplicantById(jwtPayload.getSub());
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());

        grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(mandatoryQuestionDto, grantMandatoryQuestions);

        if (mandatoryQuestionDto.getSubmissionId() != null) {
            final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(jwtPayload.getSub(), mandatoryQuestionDto.getSubmissionId());
            grantMandatoryQuestions.setSubmission(submission);
        }

        if (grantMandatoryQuestions.getStatus().equals(GrantMandatoryQuestionStatus.NOT_STARTED)) {
            grantMandatoryQuestions.setStatus(GrantMandatoryQuestionStatus.IN_PROGRESS);
        }

        if (mandatoryQuestionDto.isMandatoryQuestionsComplete()) {
            grantMandatoryQuestions.setStatus(GrantMandatoryQuestionStatus.COMPLETED);
        }

        if (mandatoryQuestionDto.getOrgType() != null && mandatoryQuestionDto.getOrgType().isPresent() &&
                (mandatoryQuestionDto.getOrgType().get().equals(LOCAL_AUTHORITY.toString())
                        || mandatoryQuestionDto.getOrgType().get().equals(INDIVIDUAL.toString())
                        || mandatoryQuestionDto.getOrgType().get().equals(OTHER.toString()))) {
            grantMandatoryQuestions.setCharityCommissionNumber(null);
            grantMandatoryQuestions.setCompaniesHouseNumber(null);
        }

        grantMandatoryQuestions.setLastUpdatedBy(applicant);
        grantMandatoryQuestions.setLastUpdated(Instant.now());

        grantMandatoryQuestionService.addMandatoryQuestionsToSubmissionObject(grantMandatoryQuestions);
        grantMandatoryQuestionService.updateMandatoryQuestion(grantMandatoryQuestions, applicant);

        log.info("Mandatory question with ID {} has been updated.", grantMandatoryQuestions.getId());

        return ResponseEntity.ok(grantMandatoryQuestionService.generateNextPageUrl(url, mandatoryQuestionId, jwtPayload.getSub()));
    }

    @GetMapping("/{mandatoryQuestionId}/application/status")
    public ResponseEntity<String> getApplicationStatusByMandatoryQuestionId(@PathVariable final UUID mandatoryQuestionId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());
        final GrantApplication grantApplication = grantMandatoryQuestions.getGrantScheme().getGrantApplication();

        return ResponseEntity.ok(grantApplication.getApplicationStatus().name());
    }
}
