package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GrantMandatoryQuestionRepository extends JpaRepository<GrantMandatoryQuestions, UUID> {
    List<GrantMandatoryQuestions> findByGrantSchemeAndCreatedBy(GrantScheme schemeId, GrantApplicant applicant);
}
