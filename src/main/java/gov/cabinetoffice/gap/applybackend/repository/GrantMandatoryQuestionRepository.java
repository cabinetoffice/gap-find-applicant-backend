package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrantMandatoryQuestionRepository extends JpaRepository<GrantMandatoryQuestions, Integer> {
    List<GrantMandatoryQuestions> findByGrantSchemeIdAndCreatedBy(Integer schemeId, GrantApplicant applicant);
}
