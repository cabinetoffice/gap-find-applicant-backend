package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.validation.annotations.ValidFundingAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGrantMandatoryQuestionDto {

    private UUID submissionId;

    private Optional<
                @NotBlank(message = "You must enter an answer")
                @Size(max = 250, message = "Organisation name must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Organisation name must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> name;

    private Optional<
                @NotBlank(message = "You must enter an answer for the address line 1")
                @Size(max = 250, message = "Address line 1 must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Address line 1 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> addressLine1;

    private Optional<
                @Size(max = 250, message = "Address line 2 must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Address line 2 must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> addressLine2;

    private Optional<
                @NotBlank(message = "You must enter an answer for the city")
                @Size(max = 250, message = "City must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = " City must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> city;


    private Optional<
                @Size(max = 250, message = "County must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "County must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> county;


    private Optional<
                @NotBlank(message = "You must enter an answer for the postcode")
                @Size(max = 8, message = "Postcode must be 8 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Postcode must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> postcode;

    private Optional<
                @Size(max = 250, message = "Charity commission number must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Charity commission number must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> charityCommissionNumber;

    private Optional<
                @Size(max = 250, message = "Companies house number must be 250 characters or less")
                @Pattern(regexp = "^(?![\\s\\S])|^[a-zA-Z0-9\\s',-]+$",
                        message = "Companies house must only use letters, numbers, and special characters such as hyphens, spaces and apostrophes")
            String> companiesHouseNumber;


    private Optional<
                @NotBlank(message = "You must select an answer")
            String> orgType;


    private Optional<
               @ValidFundingAmount
            String> fundingAmount;


    private Optional<
                @NotEmpty(message = "You must select an answer")
            List<String>> fundingLocation;

}
