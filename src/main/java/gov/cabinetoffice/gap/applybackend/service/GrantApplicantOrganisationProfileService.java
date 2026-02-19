package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.repository.GrantApplicantOrganisationProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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

    /**
     * Retrieves an organisation profile by ID and validates that the authenticated user owns it.
     * This prevents IDOR (Insecure Direct Object Reference) vulnerabilities.
     *
     * @param profileId The organisation profile ID
     * @param userId The authenticated user's ID (from JWT)
     * @return The organisation profile if the user owns it
     * @throws NotFoundException if the profile doesn't exist
     * @throws ForbiddenException if the user doesn't own the profile
     */
    public GrantApplicantOrganisationProfile getProfileByIdAndUserId(long profileId, String userId) {
        GrantApplicantOrganisationProfile profile = grantApplicantOrganisationProfileRepository
                .findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No Organisation Profile with ID %s was found", profileId)
                ));

        // Authorization check: verify the profile belongs to the authenticated user
        if (profile.getApplicant() == null) {
            log.error("Organisation profile {} has no associated applicant", profileId);
            throw new NotFoundException("Organisation profile has no associated applicant");
        }

        if (!profile.getApplicant().getUserId().equals(userId)) {
            log.warn("Authorization failed: User {} attempted to access organisation profile {} owned by user {}",
                    userId, profileId, profile.getApplicant().getUserId());
            throw new ForbiddenException("You do not have permission to access this organisation");
        }

        return profile;
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
        return profile.isComplete();
    }
}
