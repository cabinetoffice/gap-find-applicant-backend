package gov.cabinetoffice.gap.applybackend.repository;


import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Integer> {

    Optional<GrantApplication> findByGrantScheme(GrantScheme grantScheme);
}

