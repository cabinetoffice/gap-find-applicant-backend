package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GrantAdvertRepository extends JpaRepository<GrantAdvert, UUID> {
    Optional<GrantAdvert> findByContentfulSlug(String contentfulSlug);
    Optional<GrantAdvert> findBySchemeId(Integer schemeId);
}
