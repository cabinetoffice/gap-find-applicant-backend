package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.SpotlightSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpotlightSubmissionRepository extends JpaRepository<SpotlightSubmission, UUID> {

}
