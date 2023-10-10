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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

import static gov.cabinetoffice.gap.applybackend.utils.SecurityContextHelper.getUserIdFromSecurityContext;

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

    private static final String SPECIAL_CHARACTER_REGEX = "[<>\"\\\\/|?*:]";

    @GetMapping
    public ResponseEntity<List<GetSubmissionDto>> getSubmissions() {
        final String applicantId = getUserIdFromSecurityContext();
        GrantApplicant applicant = grantApplicantService.getApplicantById((applicantId));
        return ResponseEntity.ok(applicant.getSubmissions().stream()
                .map(this::buildSubmissionDto)
                .toList()
        );
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<GetSubmissionDto> getSubmission(@PathVariable final UUID submissionId) {
        final String applicantId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(buildSubmissionDto(submissionService.getSubmissionFromDatabaseBySubmissionId(applicantId, submissionId)));
    }

    @GetMapping("/{submissionId}/sections/{sectionId}")
    public ResponseEntity<SubmissionSection> getSection(@PathVariable final UUID submissionId, @PathVariable final String sectionId) {
        final String applicantId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(submissionService.getSectionBySectionId(applicantId, submissionId, sectionId));
    }

    @PostMapping("/{submissionId}/sections/{sectionId}/review")
    public ResponseEntity<String> postSectionReview(@PathVariable final UUID submissionId, @PathVariable final String sectionId, final @RequestBody @Valid SubmissionReviewBodyDto body) {
        final String applicantId = getUserIdFromSecurityContext();
        final SubmissionSectionStatus sectionStatus = submissionService.handleSectionReview(applicantId, submissionId, sectionId, body.getIsComplete());
        return ResponseEntity.ok(String.format("Section with ID %s status has been updated to %s.", sectionId, sectionStatus.toString()));
    }


    @GetMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}")
    public ResponseEntity<GetQuestionDto> getQuestion(
            @PathVariable final UUID submissionId,
            @PathVariable final String sectionId,
            @PathVariable final String questionId) {
        final String applicantId = getUserIdFromSecurityContext();

        Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(applicantId, submissionId);
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
        final String applicantId = getUserIdFromSecurityContext();
        submissionService.saveQuestionResponse(questionResponse, applicantId, submissionId, sectionId);
        return ResponseEntity.ok(submissionService.getNextNavigation(applicantId, submissionId, sectionId, questionId, false));
    }

    @GetMapping("/{submissionId}/ready")
    public ResponseEntity<Boolean> isSubmissionReadyToBeSubmitted(@PathVariable final UUID submissionId) {
        final String applicantId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(submissionService.isSubmissionReadyToBeSubmitted(applicantId, submissionId));
    }

    @GetMapping("/{submissionId}/isSubmitted")
    public ResponseEntity<Boolean> isSubmissionSubmitted(@PathVariable final UUID submissionId) {
        final String applicantId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(submissionService.hasSubmissionBeenSubmitted(applicantId, submissionId));
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitApplication(@RequestBody SubmitApplicationDto applicationSubmission) {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final GrantApplicant grantApplicant = grantApplicantService.getApplicantFromPrincipal();
        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(grantApplicant.getUserId(), applicationSubmission.getSubmissionId());
        submissionService.submit(submission, grantApplicant, jwtPayload.getEmail());

        return ResponseEntity.ok("Submitted");
    }

    @PostMapping("/createSubmission/{applicationId}")
    public ResponseEntity<CreateSubmissionResponseDto> createApplication(@PathVariable final int applicationId) throws JsonProcessingException {
        final String applicantId = getUserIdFromSecurityContext();
        final boolean isGrantApplicationPublished = grantApplicationService.isGrantApplicationPublished(applicationId);
        if (!isGrantApplicationPublished) {
            logger.debug("Grant Application {} is not been published yet.", applicationId);
            throw new GrantApplicationNotPublishedException(String.format("Grant Application %s is not been published yet.", applicationId));
        }

        final GrantApplication grantApplication = grantApplicationService.getGrantApplicationById(applicationId);
        final GrantApplicant grantApplicant = grantApplicantService.getApplicantById(applicantId);

        final boolean submissionExists = submissionService.doesSubmissionExist(grantApplicant, grantApplication);
        if (submissionExists) {
            logger.info("Grant Submission for {} already exists.", applicationId);
            throw new SubmissionAlreadyCreatedException("SUBMISSION_EXISTS");
        }

        return ResponseEntity.ok(submissionService.createSubmissionFromApplication(applicantId, grantApplicant, grantApplication));

    }

    @PutMapping("/{submissionId}/question/{questionId}/attachment/scanresult")
    public ResponseEntity<String> updateAttachment(@PathVariable final UUID submissionId,
                                                   @PathVariable final String questionId,
                                                   @RequestBody final UpdateAttachmentDto updateDetails,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        final String applicantId = getUserIdFromSecurityContext();

        secretAuthService.authenticateSecret(authHeader);

        Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(applicantId, submissionId);
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
        final String applicantId = getUserIdFromSecurityContext();
        final GrantApplicant applicant = grantApplicantService.getApplicantFromPrincipal();
        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(applicantId, submissionId);
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

        final String cleanedOriginalFilename = attachment.getOriginalFilename().replaceAll(SPECIAL_CHARACTER_REGEX, "_");
        String extension = FilenameUtils.getExtension(attachment.getOriginalFilename()).toLowerCase();
        Arrays.stream(question.getValidation().getAllowedTypes())
                .filter(item -> Objects.equals(item.toLowerCase(), extension))
                .findFirst()
                .orElseThrow(() -> new AttachmentException("The selected file must be a .DOC, .DOCX, .ODT, .PDF, .XLS, XLSX or .ZIP"));


        String fileObjKeyName = application.getId() + "/" + submissionId + "/" + questionId + "/" + cleanedOriginalFilename;
        String s3Url = attachmentService.attachmentFile(fileObjKeyName, attachment);

        GrantAttachment grantAttachment = GrantAttachment.builder()
                .filename(cleanedOriginalFilename)
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
        question.setResponse(cleanedOriginalFilename);
        this.submissionService.saveSubmission(submission);

        return ResponseEntity.ok(submissionService.getNextNavigation(applicantId, submissionId, sectionId, questionId, false));
    }

    @DeleteMapping("/{submissionId}/sections/{sectionId}/questions/{questionId}/attachments/{attachmentId}")
    public ResponseEntity<GetNavigationParamsDto> removeAttachment(@PathVariable final UUID submissionId,
                                                                 @PathVariable final String sectionId,
                                                                 @PathVariable final String questionId,
                                                                 @PathVariable final UUID attachmentId) {
        final String applicantId = getUserIdFromSecurityContext();
        final Submission submission = submissionService.getSubmissionFromDatabaseBySubmissionId(applicantId, submissionId);
        final int applicationId = submission.getApplication().getId();

        final GrantAttachment attachment = grantAttachmentService.getAttachment(attachmentId);
        attachmentService.deleteAttachment(attachment, applicationId, submissionId, questionId);
        submissionService.deleteQuestionResponse(applicantId, submissionId, questionId);
        submissionService.handleSectionReview(applicantId, submissionId, sectionId, Boolean.FALSE);

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
        final String applicantId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(submissionService.getNextNavigation(applicantId, submissionId, sectionId, questionId, saveAndExit));
    }
}
