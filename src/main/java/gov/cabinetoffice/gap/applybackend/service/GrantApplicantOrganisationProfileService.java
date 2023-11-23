package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GrantApplicantOrganisationProfileService {

    private final GrantApplicantOrganisationProfileRepository grantApplicantOrganisationProfileRepository;
    private final GrantApplicantService grantApplicantService;

    public GrantApplicantOrganisationProfile getProfileById(long profileId) {
        return grantApplicantOrganisationProfileRepository
                .findById(profileId)
                .orElseThrow(() -> new NotFoundException(String.format("No Organisation Profile with ID %s was found", profileId)));
    }

    public GrantApplicantOrganisationProfile updateOrganisation(GrantApplicantOrganisationProfile updatedProfile) {
        return grantApplicantOrganisationProfileRepository
                .findById(updatedProfile.getId())
                .map(profile -> grantApplicantOrganisationProfileRepository.save(updatedProfile))
                .orElseThrow(() -> new NotFoundException(String.format("No Organisation Profile with ID %s was found", updatedProfile.getId())));
    }

    public GrantApplicantOrganisationProfile createOrganisation(String applicantId, GrantApplicantOrganisationProfile profile) {
        final GrantApplicant applicant = grantApplicantService.getApplicantById(applicantId);
        profile.setApplicant(applicant);

        final GrantApplicantOrganisationProfile savedProfile = grantApplicantOrganisationProfileRepository.save(profile);
        applicant.setOrganisationProfile(savedProfile);

        grantApplicantService.saveApplicant(applicant);
        return savedProfile;
    }

    public Boolean isOrganisationComplete(final String sub) {
        final GrantApplicant applicant = grantApplicantService.getApplicantById(sub);
        final GrantApplicantOrganisationProfile profile = applicant.getOrganisationProfile();
        if (profile.getLegalName() == null || profile.getLegalName().isEmpty()) return false;
        if (profile.getAddressLine1() == null || profile.getAddressLine1().isEmpty()) return false;
        if (profile.getTown() == null || profile.getTown().isEmpty()) return false;
        if (profile.getPostcode() == null || profile.getPostcode().isEmpty()) return false;
        if (profile.getType() == null) return false;
        if (profile.getType() != GrantApplicantOrganisationType.INDIVIDUAL && profile.getType() != GrantApplicantOrganisationType.NON_LIMITED_COMPANY) {
            if (profile.getCompaniesHouseNumber() == null || profile.getCompaniesHouseNumber().isEmpty()) return false;
            if (profile.getCharityCommissionNumber() == null || profile.getCharityCommissionNumber().isEmpty()) return false;
        }
        return true;
    }
}
