package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrantMandatoryQuestionRepository extends JpaRepository<GrantMandatoryQuestions, UUID> {
    List<GrantMandatoryQuestions> findByGrantSchemeAndCreatedBy(GrantScheme scheme, GrantApplicant applicant);

    Optional<GrantMandatoryQuestions> findBySubmissionId(UUID submissionId);

    Optional<GrantMandatoryQuestions> findByGrantScheme(GrantScheme scheme);

    boolean existsByGrantScheme_IdAndCreatedBy_Id(Integer schemeId, long applicantId);
}
