package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetFundingOrganisationDto {
    private long id;
    private String organisationName;
}
