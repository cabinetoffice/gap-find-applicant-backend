package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionSection {
    private String sectionId;
    private String sectionTitle;
    private SubmissionSectionStatus sectionStatus;
    private List<SubmissionQuestion> questions;

    public void addQuestion(final SubmissionQuestion question) {
        if (this.getQuestions() == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }
}
