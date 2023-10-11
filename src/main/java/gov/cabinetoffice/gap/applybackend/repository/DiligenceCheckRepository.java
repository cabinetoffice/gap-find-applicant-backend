package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.DiligenceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface DiligenceCheckRepository extends JpaRepository<DiligenceCheck, UUID> {
    @Query("select count(distinct d) from DiligenceCheck d where d.applicationNumber like concat('%', ?1, '%')")
    long countDistinctByApplicationNumberContains(String applicationNumber);

}
