package gov.cabinetoffice.gap.applybackend.repository;


import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Integer> {
}

