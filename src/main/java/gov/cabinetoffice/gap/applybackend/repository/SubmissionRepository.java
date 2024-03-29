package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByApplicantId(long applicantId);
    Optional<Submission> findByApplicantIdAndApplicationId(long applicantId, Integer applicationId);
    Optional<Submission> findByIdAndApplicantUserId(UUID id, String userId);
}
