package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGrantApplicantOrganisationProfileDto {
    @Size(max = 250, message = "Organisation name must be 250 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Organisation name must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String legalName;

    private GrantApplicantOrganisationType type;

    @Size(max = 250, message = "Address line 1 must be 250 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Address line 1 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String addressLine1;

    @Size(max = 250, message = "Address line 2 must be 250 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Address line 2 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String addressLine2;

    @Size(max = 250, message = "Town or City must be 250 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Town or City must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String town;

    @Size(max = 250, message = "County must be 250 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "County must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String county;

    @Size(max = 8, message = "Postcode must be 8 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Postcode must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String postcode;

    @Size(max = 15, message = "Charity commission number must be 15 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Charity commission number must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String charityCommissionNumber;

    @Size(max = 8, message = "Companies house number must be 8 characters or less")
    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
            message = "Companies house must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String companiesHouseNumber;

}
