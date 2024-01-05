package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantAttachment;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.repository.GrantAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GrantAttachmentService {

    private final GrantAttachmentRepository grantAttachmentRepository;

    public GrantAttachment createAttachment(GrantAttachment attachment) {
        return this.grantAttachmentRepository.save(attachment);
    }

    public GrantAttachment getAttachment(UUID attachmentId) {
        return grantAttachmentRepository
                .findById(attachmentId)
                .orElseThrow(() -> new NotFoundException(String.format("No Grant Attachment with ID %s was found", attachmentId)));
    }

    public GrantAttachment getAttachmentBySubmissionAndQuestion(Submission submission, String questionId) {
        return grantAttachmentRepository
                .findBySubmissionAndQuestionId(submission, questionId)
                .orElseThrow(() -> new NotFoundException(String.format("No Grant Attachment for Submission %s and Question %s was found", submission.getId().toString(), questionId)));

    }

    public GrantAttachment save(GrantAttachment attachment) {
        return grantAttachmentRepository.save(attachment);
    }

    public void delete(GrantAttachment attachment) {
        grantAttachmentRepository.delete(attachment);
    }
}
