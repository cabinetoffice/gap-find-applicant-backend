package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrantApplicantRepository extends JpaRepository<GrantApplicant, Long> {
    Optional<GrantApplicant> findByUserId(String userid);
    boolean existsByUserId(String userId);
}
