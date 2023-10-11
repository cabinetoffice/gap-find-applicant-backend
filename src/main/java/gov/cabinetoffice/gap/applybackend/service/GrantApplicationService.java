package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GrantApplicationService {
    private final GrantApplicationRepository grantApplicationRepository;

    public GrantApplication getGrantApplicationById(final int applicationId) {
        return grantApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new NotFoundException(String.format("No Application with ID %s was found", applicationId)));
    }

    public boolean isGrantApplicationPublished(final int applicationId) {
        return getGrantApplicationById(applicationId).getApplicationStatus().equals(GrantApplicantStatus.PUBLISHED);
    }

    public boolean doesSchemeHaveApplication(final GrantScheme grantScheme) {
        return grantApplicationRepository.findByGrantScheme(grantScheme).isPresent();
    }

    public Integer getGrantApplicationId(final GrantScheme grantScheme) {
        final Optional<GrantApplication> grantApplication = grantApplicationRepository.findByGrantScheme(grantScheme);
        return grantApplication.map(GrantApplication::getId).orElse(null);
    }
}
