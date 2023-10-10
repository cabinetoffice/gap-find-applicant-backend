package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/grant-mandatory-questions")
public class GrantMandatoryQuestionsController {
    private final GrantMandatoryQuestionService grantMandatoryQuestionService;
    private final ModelMapper modelMapper;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Integer> createMandatoryQuestion(@RequestParam final Integer schemeId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.createMandatoryQuestion(schemeId, jwtPayload.getSub());
        return ResponseEntity.ok(grantMandatoryQuestions.getId());
    }

    @GetMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrantMandatoryQuestions.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<GetGrantMandatoryQuestionDto> getGrantMandatoryQuestionsById(@PathVariable final Integer grantMandatoryQuestionId) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(grantMandatoryQuestionId, jwtPayload.getSub());
        final GetGrantMandatoryQuestionDto getGrantMandatoryQuestionDto = GetGrantMandatoryQuestionDto.builder().build();
        modelMapper.map(grantMandatoryQuestions, getGrantMandatoryQuestionDto);
        return ResponseEntity.ok(getGrantMandatoryQuestionDto);
    }

    @PatchMapping("/{mandatoryQuestionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grant Mandatory question updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this mandatory question", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No Grant Mandatory question found", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> updateMandatoryQuestion(@PathVariable Integer mandatoryQuestionId,
                                                     @RequestBody @Valid UpdateGrantMandatoryQuestionDto mandatoryQuestionDto) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService.getGrantMandatoryQuestionById(mandatoryQuestionId, jwtPayload.getSub());
        modelMapper.map(mandatoryQuestionDto, grantMandatoryQuestions);
        grantMandatoryQuestions.setId(mandatoryQuestionId);
        grantMandatoryQuestionService.updateMandatoryQuestion(grantMandatoryQuestions);
        return ResponseEntity.ok(String.format("Mandatory question with ID %s has been updated.", mandatoryQuestionId));
    }
}
