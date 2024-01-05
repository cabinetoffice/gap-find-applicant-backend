package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GrantApplicantService {

    private final GrantApplicantRepository grantApplicantRepository;

    public GrantApplicant getApplicantById(final String applicantId) {
        return grantApplicantRepository
                .findByUserId(applicantId)
                .orElseThrow(() -> new NotFoundException(String.format("No Grant Applicant with ID %s was found", applicantId)));
    }

    public GrantApplicant getApplicantFromPrincipal() {
        final JwtPayload jwtPayload = (JwtPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.getApplicantById(jwtPayload.getSub());
    }

    public GrantApplicant saveApplicant(GrantApplicant applicant) {
        return grantApplicantRepository.save(applicant);
    }
}
