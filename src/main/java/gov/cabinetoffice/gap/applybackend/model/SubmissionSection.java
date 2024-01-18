package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionSection {
    private String sectionId;
    private String sectionTitle;
    private SubmissionSectionStatus sectionStatus;

    @Builder.Default
    private List<SubmissionQuestion> questions = new ArrayList<>();

    public Optional<SubmissionQuestion> optionalGetQuestionById(String questionId) {
        return this.questions
                .stream()
                .filter(question -> Objects.equals(question.getQuestionId(), questionId))
                .findFirst();
    }

    public SubmissionQuestion getQuestionById(String questionId) {
        return this.questions
                .stream()
                .filter(question -> Objects.equals(question.getQuestionId(), questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question with id " + questionId + " does not exist"));
    }

    public void addQuestion(final SubmissionQuestion question) {
        if (this.getQuestions() == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }
}
