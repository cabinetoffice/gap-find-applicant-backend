package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import gov.cabinetoffice.gap.applybackend.config.CognitoConfigProperties;
import gov.cabinetoffice.gap.applybackend.model.RegisterApplicant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateGrantApplicantService {

    private final CognitoConfigProperties cognitoProps;
    private final AWSCognitoIdentityProvider cognitoClient;

    private String createCognitoFormattedPhoneNumberFromString(final String unformattedNumber) {
        final String telephoneCountryCode = "+44";

        if (!unformattedNumber.contains(telephoneCountryCode)) {
            final String telephoneWithoutLeadingZero = unformattedNumber.substring(1);
            return telephoneCountryCode.concat(telephoneWithoutLeadingZero);
        }

        return unformattedNumber;
    }

    private AdminCreateUserResult addNewUserToCognito(final AWSCognitoIdentityProvider cognitoClient, final RegisterApplicant applicantInformation, final String temporaryPassword) {
        final String phoneNumber = createCognitoFormattedPhoneNumberFromString(applicantInformation.getTelephone());

        final AttributeType[] attributeTypes = new AttributeType[]{
                // The first two attributes are hardcoded for specific applicant roles.
                new AttributeType().withName("custom:features").withValue("user=ordinary_user"),
                new AttributeType().withName("custom:isAdmin").withValue("false"),
                new AttributeType().withName("custom:phoneNumber").withValue(phoneNumber),
                new AttributeType().withName("email").withValue(applicantInformation.getEmail()),
                new AttributeType().withName("family_name").withValue(applicantInformation.getLastName()),
                new AttributeType().withName("given_name").withValue(applicantInformation.getFirstName()),
                // This is set to conform with cola.
                new AttributeType().withName("custom:lastLogin").withValue("1970-01-01T00:00:00Z")
        };

        final AdminCreateUserRequest userRequest =
                new AdminCreateUserRequest()
                        .withUserPoolId(cognitoProps.getUserPoolId())
                        .withUsername(applicantInformation.getEmail())
                        .withUserAttributes(attributeTypes)
                        .withTemporaryPassword(temporaryPassword)
                        .withMessageAction(MessageActionType.SUPPRESS);

        return cognitoClient.adminCreateUser(userRequest);
    }

    private void verifyEmailAddress(final AWSCognitoIdentityProvider cognitoClient, final UserType user) {
        final AttributeType userAttributeEmailVerified = new AttributeType()
                .withName("email_verified")
                .withValue("true");

        final AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = new AdminUpdateUserAttributesRequest()
                .withUsername(user.getUsername())
                .withUserPoolId(cognitoProps.getUserPoolId())
                .withUserAttributes(userAttributeEmailVerified);

        cognitoClient.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
    }

    private void setPermanentPassword(final AWSCognitoIdentityProvider cognitoClient, final UserType user, final String password) {
        final AdminSetUserPasswordRequest adminSetUserPasswordRequest =
                new AdminSetUserPasswordRequest()
                        .withUsername(user.getUsername())
                        .withUserPoolId(cognitoProps.getUserPoolId())
                        .withPassword(password)
                        .withPermanent(true);

        cognitoClient.adminSetUserPassword(adminSetUserPasswordRequest);
    }

    public void createNewUser(RegisterApplicant applicantInformation) {
        // create our new user
        final AdminCreateUserResult createUserResult = addNewUserToCognito(cognitoClient, applicantInformation, cognitoProps.getUserPassword());

        // verify the email address
        verifyEmailAddress(cognitoClient, createUserResult.getUser());

        // set the account password
        setPermanentPassword(cognitoClient, createUserResult.getUser(), cognitoProps.getUserPassword());
    }

}