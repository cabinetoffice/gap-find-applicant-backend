package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetGrantApplicantOrganisationProfileDto {
    private long id;
    private String legalName;
    private String type;
    private String addressLine1;
    private String addressLine2;
    private String town;
    private String county;
    private String postcode;
    private String charityCommissionNumber;
    private String companiesHouseNumber;

}
