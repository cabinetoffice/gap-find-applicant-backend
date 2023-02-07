package gov.cabinetoffice.gap.applybackend.repository;


import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.SubmissionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Integer> {
}

