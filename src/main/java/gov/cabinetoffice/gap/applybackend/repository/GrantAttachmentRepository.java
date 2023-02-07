package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantAttachment;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GrantAttachmentRepository extends JpaRepository<GrantAttachment, UUID> {
    Optional<GrantAttachment> findBySubmissionAndQuestionId(Submission submission, String questionId);
}
