package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByApplicantId(long applicantId);
    Optional<Submission> findByApplicantIdAndApplicationId(long applicantId, Integer applicationId);
    Optional<Submission> findByIdAndApplicantUserId(UUID id, String userId);

    /**
     * Finds every submitted submission that belongs to a multi-submission application but has no
     * mandatory question record linked to it. Used by the one-off backfill to restore the
     * one-to-one relationship between a submission and its mandatory questions.
     */
    @Query("SELECT s FROM Submission s " +
            "WHERE s.status = 'SUBMITTED' " +
            "AND s.application.allowsMultipleSubmissions = true " +
            "AND NOT EXISTS (SELECT 1 FROM GrantMandatoryQuestions mq WHERE mq.submission = s)")
    List<Submission> findSubmittedMultiSubmissionWithoutMandatoryQuestions();
}
