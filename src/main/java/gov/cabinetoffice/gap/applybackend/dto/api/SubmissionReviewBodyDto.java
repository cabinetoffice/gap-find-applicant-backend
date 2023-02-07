package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionReviewBodyDto {
    @NotNull(message = "Select 'Yes, I've completed this section' or 'No, I'll come back later'")
    private Boolean isComplete;
}
