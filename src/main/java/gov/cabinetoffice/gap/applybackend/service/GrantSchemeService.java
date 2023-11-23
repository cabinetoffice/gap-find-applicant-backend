package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantSchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GrantSchemeService {

    private final GrantSchemeRepository grantSchemeRepository;

    public GrantScheme getSchemeById(Integer schemeId) {
        return grantSchemeRepository
                .findByIdWithApplicationAndAdverts(schemeId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No Grant Scheme with ID %s was found", schemeId)));
    }
}
