package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSubmissionDto {
    private Integer grantSchemeId;
    private Integer grantApplicationId;
    private UUID grantSubmissionId;
    private String applicationName;
    private SubmissionStatus submissionStatus;
    private ZonedDateTime submittedDate;
    @Builder.Default
    private List<GetSectionDto> sections = new ArrayList<>();
}
