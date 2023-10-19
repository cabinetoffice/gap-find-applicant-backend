package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantApplicantOrganisationProfileMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantMandatoryQuestionService {
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    private final GrantApplicantOrganisationProfileMapper organisationProfileMapper;

    public GrantMandatoryQuestions getGrantMandatoryQuestionById(UUID id, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", id))));

        if (grantMandatoryQuestion.isPresent() && !grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
            throw new ForbiddenException(String.format("Mandatory Question with ID %s was not created by %s", id, applicantSub));
        }

        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions createMandatoryQuestion(GrantScheme scheme, GrantApplicant applicant) {

        // TODO we probably don't need to ask if it exists and instead react if the result is null
        //TODO definitely a valid use case for both here
        //the create method should error out if it's trying to create a mandatory question which already exists
        //however in our usage, there is a benefit to us doing a create-or-retrieve as that is the process we'd want in the frontend.
        //might be worth having two methods, a create method which errors out if it already exists, and a create-or-retrieve method
        // which uses the previous one and does the retrieval if it errors.
        if (doesMandatoryQuestionAlreadyExist(scheme, applicant)) {
            log.debug("Mandatory question for scheme {}, and applicant {} already exist", scheme.getId(), applicant.getId());
            return grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).get(0);
        }
        final GrantApplicantOrganisationProfile organisationProfile = applicant.getOrganisationProfile();

        final GrantMandatoryQuestions grantMandatoryQuestions = organisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(organisationProfile);
        grantMandatoryQuestions.setGrantScheme(scheme);
        grantMandatoryQuestions.setCreatedBy(applicant);

        return grantMandatoryQuestionRepository.save(grantMandatoryQuestions);
    }


    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions) {
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId()) //TODO there is no need for the additional database call here
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", grantMandatoryQuestions.getId())));
    }


    public String generateNextPageUrl(String url, GrantMandatoryQuestions mandatoryQuestion) {
        final Map<String, String> mapper = new HashMap<>();
        mapper.put("organisation-name", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-address");
        mapper.put("organisation-address", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-type");
        mapper.put("organisation-type", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-companies-house-number");
        mapper.put("organisation-companies-house-number", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-charity-commission-number");
        mapper.put("organisation-charity-commission-number", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-funding-amount");
        mapper.put("organisation-funding-amount", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-funding-location");
        mapper.put("organisation-funding-location", "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-summary");

        final String[] urlParts = url.split("/");
        //takes the last part of the url and strips it of eventual queryParams
        final String questionPage = urlParts[urlParts.length - 1].split("\\?")[0];
        return mapper.get(questionPage);
    }

    private boolean doesMandatoryQuestionAlreadyExist(GrantScheme scheme, GrantApplicant applicant) {
        return !grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).isEmpty();
    }


}
