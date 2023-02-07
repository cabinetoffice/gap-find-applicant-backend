package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetQuestionNavigationDto {
    private String sectionId;
    private String questionId;
}
