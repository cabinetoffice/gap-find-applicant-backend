package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
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

    public GrantMandatoryQuestions getGrantMandatoryQuestionById(UUID id, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", id))));

        if (!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)) {
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

        final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                .grantScheme(scheme)
                .createdBy(applicant)
                .build();

        return grantMandatoryQuestionRepository.save(grantMandatoryQuestions);
    }

    private boolean doesMandatoryQuestionAlreadyExist(GrantScheme scheme, GrantApplicant applicant) {
        return !grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).isEmpty();
    }

    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions) {
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId()) //TODO there is no need for the additional database call here
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", grantMandatoryQuestions.getId())));
    }

    public String generateNextPageUrl(final GrantMandatoryQuestions mandatoryQuestion) {
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null &&
                mandatoryQuestion.getOrgType() != null &&
                mandatoryQuestion.getCompaniesHouseNumber() != null &&
                mandatoryQuestion.getCharityCommissionNumber() != null &&
                mandatoryQuestion.getFundingAmount() != null &&
                mandatoryQuestion.getFundingLocation() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-summary";
        }
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null &&
                mandatoryQuestion.getOrgType() != null &&
                mandatoryQuestion.getCompaniesHouseNumber() != null &&
                mandatoryQuestion.getCharityCommissionNumber() != null &&
                mandatoryQuestion.getFundingAmount() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-funding-location";
        }
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null &&
                mandatoryQuestion.getOrgType() != null &&
                mandatoryQuestion.getCompaniesHouseNumber() != null &&
                mandatoryQuestion.getCharityCommissionNumber() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-funding-amount";
        }
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null &&
                mandatoryQuestion.getOrgType() != null &&
                mandatoryQuestion.getCompaniesHouseNumber() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-charity-commission-number";
        }
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null &&
                mandatoryQuestion.getOrgType() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-companies-house-number";
        }
        if (mandatoryQuestion.getName() != null &&
                mandatoryQuestion.getAddressLine1() != null &&
                mandatoryQuestion.getCity() != null &&
                mandatoryQuestion.getPostcode() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-type";
        }
        if (mandatoryQuestion.getName() != null) {
            return "/mandatory-questions/" + mandatoryQuestion.getId() + "/organisation-address";
        }
        return "";
    }

    public boolean isPageAlreadyAnswered(String url, GetGrantMandatoryQuestionDto mandatoryQuestion) {
        final Map<String, Object> mapper = new HashMap<>();
        mapper.put("organisation-name", "name");
        mapper.put("organisation-address", new String[]{"addressLine1", "city", "postcode"});
        mapper.put("organisation-type", "orgType");
        mapper.put("organisation-companies-house-number", "companiesHouseNumber");
        mapper.put("organisation-charity-commission-number", "charityCommissionNumber");
        mapper.put("organisation-funding-amount", "fundingAmount");
        mapper.put("organisation-funding-location", "fundingLocation");

        Object questionKey = getMandatoryQuestionKeyFromUrl(url, mapper);
        if (questionKey == null) {
            return false;
        }
        if (questionKey instanceof String[] keys) {
            for (String key : keys) {
                if (getValueFromKey(mandatoryQuestion, key) == null) {
                    return false;
                }
            }
            return true;
        } else {
            return getValueFromKey(mandatoryQuestion, (String) questionKey) != null;
        }

    }


    public Object getMandatoryQuestionKeyFromUrl(String url, Map<String, Object> mapper) {
        String[] parts = url.split("/");
        String question = parts[parts.length - 1].split("\\?")[0];
        Object questionKey = mapper.get(question);
        return questionKey;
    }

    public Object getValueFromKey(GetGrantMandatoryQuestionDto mandatoryQuestionDto, String key) {
        switch (key) {
            case "name":
                return mandatoryQuestionDto.getName();
            case "addressLine1":
                return mandatoryQuestionDto.getAddressLine1();
            case "city":
                return mandatoryQuestionDto.getCity();
            case "postcode":
                return mandatoryQuestionDto.getPostcode();
            case "orgType":
                return mandatoryQuestionDto.getOrgType();
            case "companiesHouseNumber":
                return mandatoryQuestionDto.getCompaniesHouseNumber();
            case "charityCommissionNumber":
                return mandatoryQuestionDto.getCharityCommissionNumber();
            case "fundingAmount":
                return mandatoryQuestionDto.getFundingAmount();
            case "fundingLocation":
                return mandatoryQuestionDto.getFundingLocation();
            default:
                return null;
        }
    }


}
