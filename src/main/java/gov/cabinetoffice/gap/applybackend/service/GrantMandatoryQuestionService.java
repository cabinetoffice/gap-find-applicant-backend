package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.exception.ForbiddenException;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantMandatoryQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantMandatoryQuestionService {
    private final GrantMandatoryQuestionRepository grantMandatoryQuestionRepository;
    private final GrantApplicantService grantApplicantService;
    private final GrantSchemeService grantSchemeService;


    public GrantMandatoryQuestions getGrantMandatoryQuestionById(Integer id, String applicantSub) {
        final Optional<GrantMandatoryQuestions> grantMandatoryQuestion = ofNullable(grantMandatoryQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", id))));
        if(!grantMandatoryQuestion.get().getCreatedBy().getUserId().equals(applicantSub)){
            throw new ForbiddenException(String.format("Mandatory Question with ID %s was not created by %s", id, applicantSub));
        }
        return grantMandatoryQuestion.get();
    }

    public GrantMandatoryQuestions createMandatoryQuestion(Integer schemeId, String applicantSub){
        final GrantApplicant applicant = grantApplicantService.getApplicantById(applicantSub);
        final GrantScheme scheme = grantSchemeService.getSchemeById(schemeId);

        if(doesMandatoryQuestionAlreadyExist(schemeId, applicant)){
            log.debug("Mandatory question for scheme {}, and applicant {} already exist", schemeId, applicantSub);
            return grantMandatoryQuestionRepository.findByGrantSchemeIdAndCreatedBy(schemeId, applicant).get(0);
        };

        final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                .grantScheme(scheme)
                .createdBy(applicant)
                .build();

        return grantMandatoryQuestionRepository.save(grantMandatoryQuestions);
    }

    private boolean doesMandatoryQuestionAlreadyExist(Integer schemeId, GrantApplicant applicant) {
        return  !grantMandatoryQuestionRepository.findByGrantSchemeIdAndCreatedBy(schemeId, applicant).isEmpty();
    }

    public GrantMandatoryQuestions updateMandatoryQuestion(GrantMandatoryQuestions grantMandatoryQuestions) {
        return grantMandatoryQuestionRepository
                .findById(grantMandatoryQuestions.getId())
                .map(mandatoryQuestion -> grantMandatoryQuestionRepository.save(grantMandatoryQuestions))
                .orElseThrow(() -> new NotFoundException(String.format("No Mandatory Question with ID %s was found", grantMandatoryQuestions.getId())));
    }
}
