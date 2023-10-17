package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import static java.util.Optional.ofNullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public GrantMandatoryQuestions createMandatoryQuestion(GrantScheme scheme, GrantApplicant applicant){

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
        return  !grantMandatoryQuestionRepository.findByGrantSchemeAndCreatedBy(scheme, applicant).isEmpty();
    }

    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions) {
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId()) //TODO there is no need for the additional database call here
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", grantMandatoryQuestions.getId())));
    }
}
