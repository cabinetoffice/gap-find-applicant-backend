package gov.cabinetoffice.gap.applybackend.repository;

import gov.cabinetoffice.gap.applybackend.model.FundingOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingOrganisationRepository extends JpaRepository<FundingOrganisation, Integer> {
}
