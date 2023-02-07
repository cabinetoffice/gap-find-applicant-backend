package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.FundingOrganisation;
import gov.cabinetoffice.gap.applybackend.repository.FundingOrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FundingOrganisationService {

    private final FundingOrganisationRepository fundingOrganisationRepository;

    public FundingOrganisation getFundingOrganisationById(Integer id) {
        return fundingOrganisationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("No Funder with ID %s was found", id)));
    }
}
