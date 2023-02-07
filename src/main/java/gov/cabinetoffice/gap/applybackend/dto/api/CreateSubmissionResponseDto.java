package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubmissionResponseDto {

    private boolean submissionCreated;

    private UUID submissionId;
}
