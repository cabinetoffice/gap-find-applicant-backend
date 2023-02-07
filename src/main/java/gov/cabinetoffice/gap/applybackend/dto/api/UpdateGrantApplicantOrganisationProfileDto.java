package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGrantApplicantOrganisationProfileDto {
    private String legalName;
    private GrantApplicantOrganisationType type;
    private String addressLine1;
    private String addressLine2;
    private String town;
    private String county;
    private String postcode;
    private String charityCommissionNumber;
    private String companiesHouseNumber;

}
