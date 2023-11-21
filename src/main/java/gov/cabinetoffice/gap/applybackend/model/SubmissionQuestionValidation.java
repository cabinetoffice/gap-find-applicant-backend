package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionQuestionValidation {
    private boolean mandatory;
    private Integer minLength;
    private Integer maxLength;
    private Integer minWords;
    private Integer maxWords;
    private Boolean greaterThanZero;
    private String validInput; // TODO convert to enum when we know all options
    private int maxFileSizeMB;
    private String[] allowedTypes;
    private boolean disallowSpecialChars;
}
