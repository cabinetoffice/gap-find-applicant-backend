package gov.cabinetoffice.gap.applybackend.repository;


import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Integer> {
    Optional<GrantApplication> getGrantApplicationByGrantSchemeId(int schemeId);
}

