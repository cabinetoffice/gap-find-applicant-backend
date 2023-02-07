package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionQuestion {
    private UUID attachmentId;
    private String questionId;
    private String profileField;
    private String fieldTitle;
    private String displayText;
    private String hintText;
    private String questionSuffix;
    private String fieldPrefix;
    private String adminSummary;
    private SubmissionQuestionResponseType responseType;
    private SubmissionQuestionValidation validation;
    private String[] options;
    private String response;
    private String[] multiResponse;

}
