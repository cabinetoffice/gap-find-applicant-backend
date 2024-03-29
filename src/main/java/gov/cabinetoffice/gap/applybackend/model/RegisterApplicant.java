package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.validation.annotations.ContainsOnlyAlphaChars;
import gov.cabinetoffice.gap.applybackend.validation.annotations.EmailAddressesMatch;
import gov.cabinetoffice.gap.applybackend.validation.annotations.PhoneNumberIsValid;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@EmailAddressesMatch.List({
        @EmailAddressesMatch(
                field = "email",
                fieldMatch = "emailConfirmed",
                message = "Email addresses must match"
        ),
})
public class RegisterApplicant {
    @NotBlank(message = "Enter a first name")
    @ContainsOnlyAlphaChars(message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Enter a last name")
    @ContainsOnlyAlphaChars(message = "Last name must contain only letters")
    private String lastName;

    @NotBlank(message = "Enter an email address")
    @Email(message = "Enter an email address in the correct format, like name@example.com")
    @Size(max = 254, message = "Email address must be 254 characters or less")
    private String email;

    @NotBlank(message = "Enter an email address")
    @Email(message = "Enter an email address in the correct format, like name@example.com")
    @Size(max = 254, message = "Email address must be 254 characters or less")
    private String emailConfirmed;

    @PhoneNumberIsValid(message = "Enter a UK telephone number, like 07123456789", field = "telephone")
    private String telephone;

    @NotBlank(message = "You must confirm that you have read and agreed to the privacy policy")
    private String privacyPolicy;
}
