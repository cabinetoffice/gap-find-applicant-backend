package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGrantMandatoryQuestionDto {

    private UUID submissionId;

    //    @NotBlank(message = "You must enter an answer")
//    @Size(max = 250, message = "Organisation name must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Organisation name must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String name;

    //    @NotBlank(message = "You must enter an answer for the address line 1")
//    @Size(max = 250, message = "Address line 1 must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Address line 1 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String addressLine1;

    //    @Size(max = 250, message = "Address line 2 must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Address line 2 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String addressLine2;

    //    @NotBlank(message = "You must enter an answer for the city")
//    @Size(max = 250, message = "City must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = " City must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String city;

    //    @Size(max = 250, message = "County must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "County must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String county;

    //    @NotBlank(message = "You must enter an answer for the postcode")
//    @Size(max = 8, message = "Postcode must be 8 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Postcode must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String postcode;

    //    @Size(max = 250, message = "Charity commission number must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Charity commission number must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String charityCommissionNumber;

    //    @Size(max = 250, message = "Companies house number must be 250 characters or less")
//    @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
//            message = "Companies house must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
    private String companiesHouseNumber;

    //    @NotBlank(message = "You must select an answer")
    private String orgType;

    //    @NotBlank(message = "You must enter an answer")
//    @Pattern(regexp = "^[0-9]+[.][0-9]{1,2}$",message = "Funding amount must only contain numbers, and decimal(max 2) will need to be split with a dot")
    private String fundingAmount;

    //    @NotEmpty(message = "You must select an answer")
    private List<String> fundingLocation;
}
