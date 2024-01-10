package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.validation.annotations.ValidQuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@ValidQuestionResponse
@Builder
@Data
public class CreateQuestionResponseDto {
    private UUID submissionId;
    private String questionId;
    private String response;
    private String[] multiResponse;
    private Boolean sectionComplete;
}
