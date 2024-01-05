package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GrantSchemeRepository extends JpaRepository<GrantScheme, Integer> {
    @Query("select g from GrantScheme g where g.id = ?1")
    @EntityGraph(attributePaths = {"grantApplication", "grantAdverts"})
    Optional<GrantScheme> findByIdWithApplicationAndAdverts(Integer integer);
}
