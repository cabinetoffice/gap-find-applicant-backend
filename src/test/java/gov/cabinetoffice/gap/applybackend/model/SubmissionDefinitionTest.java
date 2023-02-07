package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)
class SubmissionDefinitionTest {
    @InjectMocks
    private SubmissionDefinition serviceUnderTest;

    @Test
    void returnsCorrectSubmissionDefinition() throws JsonProcessingException {

//        Call function
//        Assert return equals submission definition
        ApplicationFormQuestion applicationFormQuestion = ApplicationFormQuestion.builder()
                .questionId(null)
                .fieldTitle(null)
                .responseType(null)
                .validation(null)
                .options(null)
                .questionSuffix(null)
                .profileField(null)
                .adminSummary(null)
                .build();

        ApplicationFormSection applicationFormSectionNormalSection = ApplicationFormSection.builder()
                .sectionId("Section_ID")
                .sectionStatus("NOT_STARTED")
                .questions(List.of(applicationFormQuestion))
                .build();

        ApplicationFormSection applicationFormSectionEligibility = ApplicationFormSection.builder()
                .sectionId("ELIGIBILITY")
                .sectionStatus("NOT_STARTED")
                .questions(List.of(applicationFormQuestion))
                .build();

        ApplicationDefinition applicationDefinition = ApplicationDefinition.builder()
                .sections(List.of(applicationFormSectionNormalSection, applicationFormSectionEligibility))
                .build();

        SubmissionQuestion questions = SubmissionQuestion.builder()
                .questionId(null)
                .validation(null)
                .profileField(null)
                .displayText(null)
                .hintText(null)
                .questionSuffix(null)
                .fieldTitle(null)
                .response(null)
                .multiResponse(null)
                .build();

        SubmissionSection submissionSectionNormalSection = SubmissionSection.builder()
                .sectionId("Section_ID")
                .sectionStatus(SubmissionSectionStatus.CANNOT_START_YET)
                .questions(List.of(questions))
                .build();

        SubmissionSection submissionSectionEligibilitySection = SubmissionSection.builder()
                .sectionId("ELIGIBILITY")
                .sectionStatus(SubmissionSectionStatus.NOT_STARTED)
                .questions(List.of(questions))
                .build();

        SubmissionDefinition submissionDefinition = SubmissionDefinition.builder()
                .sections(List.of(submissionSectionNormalSection, submissionSectionEligibilitySection))
                .build();

        final SubmissionDefinition methodResponse = serviceUnderTest.transformApplicationDefinitionToSubmissionOne(applicationDefinition);
        assertEquals("equals", methodResponse, submissionDefinition);
    }
}
