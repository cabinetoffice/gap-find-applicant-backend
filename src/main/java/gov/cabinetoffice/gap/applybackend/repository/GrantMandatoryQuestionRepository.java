package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantMandatoryQuestionRepository extends JpaRepository<GrantMandatoryQuestions, Integer> {

}
