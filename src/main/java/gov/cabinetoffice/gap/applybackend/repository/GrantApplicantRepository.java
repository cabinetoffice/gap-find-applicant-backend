package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GrantApplicantRepository extends JpaRepository<GrantApplicant, Long> {
    Optional<GrantApplicant> findByUserId(String userid);
}
