package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GrantBeneficiaryRepository extends JpaRepository<GrantBeneficiary, UUID> {
    Optional<GrantBeneficiary> findBySubmissionId(UUID submissionId);

}
