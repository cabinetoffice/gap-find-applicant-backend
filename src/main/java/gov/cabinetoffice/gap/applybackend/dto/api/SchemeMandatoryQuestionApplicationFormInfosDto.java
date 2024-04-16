package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeMandatoryQuestionApplicationFormInfosDto {
    private boolean hasInternalApplication;
    private boolean hasPublishedInternalApplication;
}
