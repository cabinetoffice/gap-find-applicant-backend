package gov.cabinetoffice.gap.applybackend.repository;


import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Integer> {
    Optional<GrantApplication> getGrantApplicationByGrantSchemeId(int schemeId);

    Optional<GrantApplication> findByGrantScheme(GrantScheme grantScheme);


    @NotNull
    @EntityGraph(attributePaths = {"grantScheme"})
    @Query("select g from GrantApplication g where g.id = ?1")
    @Override
    Optional<GrantApplication> findById(@NotNull Integer integer);
}

