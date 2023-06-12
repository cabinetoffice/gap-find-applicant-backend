package gov.cabinetoffice.gap.applybackend.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cabinetoffice.gap.applybackend.constants.APIConstants;
import gov.cabinetoffice.gap.applybackend.dto.api.*;
import gov.cabinetoffice.gap.applybackend.enums.GrantAttachmentStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.exception.*;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

// TODO This class could probably be broken up into a few smaller more targeted classes
@RequiredArgsConstructor
@RequestMapping("/submissions")
@RestController
public class SubmissionController {

    private final SubmissionService submissionService;
    private final GrantApplicantService grantApplicantService;
    private final GrantAttachmentService grantAttachmentService;
    private final GrantApplicationService grantApplicationService;

    private final SecretAuthService secretAuthService;
    private final AttachmentService attachmentService;
    private final Logger logger = LoggerFactory.getLogger(SubmissionController.class);
    private final Clock clock;

    @GetMapping
    public ResponseEntity<List<GetSubmissionDto>> getSubmissions() {
        JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID applicantId = UUID.fromString(jwtPayload.getSub());
        GrantApplicant applicant = grantApplicantService.getApplicantById((applicantId));
        return ResponseEntity.ok(applicant.getSubmissions().stream()
                .map(this::buildSubmissionDto)
                .toList()
        );
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<GetSubmissionDto> getSubmission(@PathVariable final UUID submissionId) {
        return ResponseEntity.ok(buildSubmissionDto(submissionService.getSubmissionFromDatabaseBySubmissionId(submissionId)));
    }

    @GetMapping("/{submissionId}/sections/{sectionId}")
    public ResponseEntity<SubmissionSection> getSection(@PathVariable final UUID submissionId, @PathVariable final String sectionId) {
        return ResponseEntity.ok(submissionService.getSectionBySectionId(submissionId, sectionId));
    }

    @PostMapping("/{submissionId}/sections/{sectionId}/review")
    public ResponseEntity<String> postSectionReview(@PathVariable final UUID submissionId, @PathVariable final String sectionId, final @RequestBody @Valid SubmissionReviewBodyDto body) {
        final SubmissionSectionStatus sectionStatus = submissionService.handleSectionReview(submissionId, sectionId, body.getIsComplete());
        return ResponseEntity.ok(String.format("Section with ID %s status has been updated to %s.", sectionId, sectionStatus.toString()));
    }


    @GetMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}")
    public ResponseEntity<GetQuestionDto> getQuestion(
            @PathVariable final UUID submissionId,
            @PathVariable final String sectionId,
            @PathVariable final String questionId) {

        Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(submissionId);
        SubmissionSection section = submission
                .getDefinition()
                .getSections()
                .stream().filter(s -> s.getSectionId().equals(sectionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("No Section with ID %s was found", sectionId)));

        SubmissionQuestion question = section
                .getQuestions()
                .stream().filter(q -> q.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("No question with ID %s was found", questionId)));


        int indexOfQuestion = section.getQuestions().indexOf(question);
        int indexOfPrevious = indexOfQuestion - 1;
        int indexOfNext = indexOfQuestion + 1;
        GetQuestionNavigationDto nextNavigation = null;
        GetQuestionNavigationDto previousNavigation = null;

        if (indexOfPrevious >= 0) {
            previousNavigation = GetQuestionNavigationDto.builder()
                    .sectionId(sectionId)
                    .questionId(section.getQuestions().get(indexOfPrevious).getQuestionId())
                    .build();
        }

        if (indexOfNext < section.getQuestions().size()) {
            nextNavigation = GetQuestionNavigationDto.builder()
                    .sectionId(sectionId)
                    .questionId(section.getQuestions().get(indexOfNext).getQuestionId())
                    .build();
        }

        GetQuestionDto exemplarQuestionDto = GetQuestionDto.builder()
                .grantApplicationId(submission.getApplication().getId())
                .grantSchemeId(submission.getScheme().getId())
                .grantSubmissionId(submission.getId())
                .sectionId(section.getSectionId())
                .sectionTitle(section.getSectionTitle())
                .question(question)
                .nextNavigation(nextNavigation)
                .previousNavigation(previousNavigation)
                .build();

        return ResponseEntity.ok(exemplarQuestionDto);
    }

    private GetSubmissionDto buildSubmissionDto(Submission submission) {
        List<GetSectionDto> sections = new ArrayList<>();
        for (SubmissionSection section : submission.getDefinition().getSections()) {
            List<String> questionIds = section.getQuestions().stream().map(SubmissionQuestion::getQuestionId).toList();
            sections.add(GetSectionDto.builder()
                    .sectionId(section.getSectionId())
                    .sectionStatus(section.getSectionStatus().toString())
                    .sectionTitle(section.getSectionTitle())
                    .questionIds(questionIds)
                    .build());
        }

        return GetSubmissionDto.builder()
                .grantSubmissionId(submission.getId())
                .grantApplicationId(submission.getApplication().getId())
                .grantSchemeId(submission.getScheme().getId())
                .applicationName(submission.getApplicationName())
                .submissionStatus(submission.getStatus())
                .sections(sections)
                .build();
    }

    @PostMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}")
    public ResponseEntity<GetNavigationParamsDto> save(@PathVariable final UUID submissionId,
                                                       @PathVariable final String sectionId,
                                                       @PathVariable final String questionId,
                                                       @Valid @RequestBody CreateQuestionResponseDto questionResponse) {
        submissionService.saveQuestionResponse(questionResponse, submissionId, sectionId);
        return ResponseEntity.ok(submissionService.getNextNavigation(submissionId, sectionId, questionId, false));
    }

    @GetMapping("/{submissionId}/ready")
    public ResponseEntity<Boolean> isSubmissionReadyToBeSubmitted(@PathVariable final UUID submissionId) {
        return ResponseEntity.ok(submissionService.isSubmissionReadyToBeSubmitted(submissionId));
    }

    @GetMapping("/{submissionId}/isSubmitted")
    public ResponseEntity<Boolean> isSubmissionSubmitted(@PathVariable final UUID submissionId) {
        return ResponseEntity.ok(submissionService.hasSubmissionBeenSubmitted(submissionId));
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitApplication(@RequestBody SubmitApplicationDto applicationSubmission) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(applicationSubmission.getSubmissionId());
        submissionService.submit(submission, jwtPayload.getEmail());

        return ResponseEntity.ok("Submitted");
    }

    @PostMapping("/createSubmission/{applicationId}")
    public ResponseEntity<CreateSubmissionResponseDto> createApplication(@PathVariable final int applicationId) throws JsonProcessingException {
        final boolean isGrantApplicationPublished = grantApplicationService.isGrantApplicationPublished(applicationId);
        if (!isGrantApplicationPublished) {
            logger.debug("Grant Application {} is not been published yet.", applicationId);
            throw new GrantApplicationNotPublishedException(String.format("Grant Application %s is not been published yet.", applicationId));
        }

        final GrantApplication grantApplication = grantApplicationService.getGrantApplicationById(applicationId);
        JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = UUID.fromString(jwtPayload.getSub());
        final GrantApplicant grantApplicant = grantApplicantService.getApplicantById(userId);

        final boolean submissionExists = submissionService.doesSubmissionExist(grantApplicant, grantApplication);
        if (submissionExists) {
            logger.info("Grant Submission for {} already exists.", applicationId);
            throw new SubmissionAlreadyCreatedException("SUBMISSION_EXISTS");
        }

        return ResponseEntity.ok(submissionService.createSubmissionFromApplication(grantApplicant, grantApplication));

    }

    @PutMapping("/{submissionId}/question/{questionId}/attachment/scanresult")
    public ResponseEntity<String> updateAttachment(@PathVariable final UUID submissionId,
                                                   @PathVariable final String questionId,
                                                   @RequestBody final UpdateAttachmentDto updateDetails,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {

        secretAuthService.authenticateSecret(authHeader);

        Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(submissionId);
        GrantAttachment attachment = grantAttachmentService.getAttachmentBySubmissionAndQuestion(submission, questionId);

        attachment.setLastUpdated(Instant.now(clock));
        attachment.setLocation(updateDetails.getUri());
        if (Boolean.TRUE.equals(updateDetails.getIsClean())) {
            attachment.setStatus(GrantAttachmentStatus.AVAILABLE);
        } else {
            attachment.setStatus(GrantAttachmentStatus.QUARANTINED);
        }

        grantAttachmentService.save(attachment);
        return ResponseEntity.ok("Attachment Updated");
    }

    @PostMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}/attach")
    public ResponseEntity<GetNavigationParamsDto> postAttachment(@PathVariable final UUID submissionId,
                                                                 @PathVariable final String sectionId,
                                                                 @PathVariable final String questionId,
                                                                 @RequestBody final MultipartFile attachment) {
        final GrantApplicant applicant = grantApplicantService.getApplicantFromPrincipal();
        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(submissionId);
        final GrantApplication application = submission.getApplication();
        final SubmissionQuestion question = submission.getQuestion(sectionId, questionId);

        if (attachment == null) {
            throw new AttachmentException("Select a file to continue");
        }

        if (attachment.getSize() == 0) {
            throw new AttachmentException("The selected file is empty");
        }

        if (question.getAttachmentId() != null) {
            throw new AttachmentException("You can only select up to 1 file at the same time");
        }

        String extension = FilenameUtils.getExtension(attachment.getOriginalFilename()).toLowerCase();
        Arrays.stream(question.getValidation().getAllowedTypes())
                .filter(item -> Objects.equals(item.toLowerCase(), extension))
                .findFirst()
                .orElseThrow(() -> new AttachmentException("The selected file must be a .DOC, .DOCX, .ODT, .PDF, .XLS, XLSX or .ZIP"));


        String fileObjKeyName = application.getId() + "/" + submissionId + "/" + questionId + "/" + attachment.getOriginalFilename();
        String s3Url = attachmentService.attachmentFile(fileObjKeyName, attachment);

        GrantAttachment grantAttachment = GrantAttachment.builder()
                .filename(attachment.getOriginalFilename())
                .location(s3Url)
                .questionId(questionId)
                .createdBy(applicant)
                .lastUpdated(Instant.now())
                .status(GrantAttachmentStatus.AWAITING_SCAN)
                .version(1)
                .submission(submission)
                .build();
        this.grantAttachmentService.createAttachment(grantAttachment);

        question.setAttachmentId(grantAttachment.getId());
        question.setResponse(attachment.getOriginalFilename());
        this.submissionService.saveSubmission(submission);

        return ResponseEntity.ok(submissionService.getNextNavigation(submissionId, sectionId, questionId, false));
    }

    @DeleteMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}/attachments/{attachmentId}")
    public ResponseEntity<GetNavigationParamsDto> removeAttachment(@PathVariable final UUID submissionId,
                                                                 @PathVariable final String sectionId,
                                                                 @PathVariable final String questionId,
                                                                 @PathVariable final UUID attachmentId) {

        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(submissionId);
        final int applicationId = submission.getApplication().getId();

        final GrantAttachment attachment = grantAttachmentService.getAttachment(attachmentId);
        attachmentService.deleteAttachment(attachment, applicationId, submissionId, questionId);
        submissionService.deleteQuestionResponse(submissionId, questionId);
        submissionService.handleSectionReview(submissionId, sectionId, Boolean.FALSE);

        final GetNavigationParamsDto nextNav = GetNavigationParamsDto.builder()
                .responseAccepted(Boolean.TRUE)
                .nextNavigation(Map.of(
                        APIConstants.NAVIGATION_SECTION_ID, sectionId,
                        APIConstants.NAVIGATION_QUESTION_ID, questionId
                ))
                .build();

        return ResponseEntity.ok(nextNav);
    }

    @GetMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}/next-navigation")
    public ResponseEntity<GetNavigationParamsDto> getNextNavigationForQuestion(@PathVariable final UUID submissionId,
                                                                               @PathVariable final String sectionId,
                                                                               @PathVariable final String questionId,
                                                                               @RequestParam(required = false, defaultValue = "false") final boolean saveAndExit) {
        return ResponseEntity.ok(submissionService.getNextNavigation(submissionId, sectionId, questionId, saveAndExit));
    }
}
