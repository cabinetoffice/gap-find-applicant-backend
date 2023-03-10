package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import gov.cabinetoffice.gap.applybackend.config.CognitoConfigProperties;
import gov.cabinetoffice.gap.applybackend.model.RegisterApplicant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGrantApplicantServiceTest {

    private CognitoConfigProperties cognitoProps;
    @Mock
    private AWSCognitoIdentityProvider cognitoClient;
    private CreateGrantApplicantService serviceUnderTest;

    @Captor
    private ArgumentCaptor<AdminCreateUserRequest> adminUserRequestCaptor;

    @Captor
    private ArgumentCaptor<AdminUpdateUserAttributesRequest> emailVerificationCaptor;

    @Captor
    private ArgumentCaptor<AdminSetUserPasswordRequest> passwordResetCaptor;

    @BeforeEach
    void setup() {
        cognitoProps = CognitoConfigProperties.builder()
                .accessKey("an-access-key")
                .secretKey("a-secret-key")
                .userPoolId("a-user-pool-id")
                .region("eu-west-2")
                .userPassword("a-user-password")
                .build();

        serviceUnderTest = new CreateGrantApplicantService(cognitoProps, cognitoClient);
    }

    @Test
    void createNewUser_CreatesUserInCognito() {

        final String unformattedTelephoneNumber = "00000000000";
        final RegisterApplicant applicantToCreate = RegisterApplicant.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith.test@cabinetoffice.gov.uk")
                .emailConfirmed("john.smith.test@cabinetoffice.gov.uk")
                .telephone(unformattedTelephoneNumber)
                .build();

        final UserType createdUser = new UserType();
        createdUser.setUserCreateDate(new Date());
        createdUser.setUsername("john.smith.test@cabinetoffice.gov.uk");

        final AdminCreateUserResult createUserResult = new AdminCreateUserResult()
                .withUser(createdUser);

        when(cognitoClient.adminCreateUser(Mockito.any()))
                .thenReturn(createUserResult);

        serviceUnderTest.createNewUser(applicantToCreate);

        assertUserIsCreated(applicantToCreate, unformattedTelephoneNumber);
        assertEmailAddressIsVerified(createdUser);
        assertPasswordIsReset(createdUser);
    }

    private void assertPasswordIsReset(UserType createdUser) {

        verify(cognitoClient).adminSetUserPassword(passwordResetCaptor.capture());

        final AdminSetUserPasswordRequest attributes = passwordResetCaptor.getValue();
        assertThat(attributes.getUserPoolId()).isEqualTo(cognitoProps.getUserPoolId());
        assertThat(attributes.getUsername()).isEqualTo(createdUser.getUsername());
        assertThat(attributes.getPermanent()).isTrue();
        assertThat(attributes.getPassword()).isEqualTo(cognitoProps.getUserPassword());
    }

    private void assertEmailAddressIsVerified(UserType user) {

        verify(cognitoClient).adminUpdateUserAttributes(emailVerificationCaptor.capture());

        final AdminUpdateUserAttributesRequest attributes = emailVerificationCaptor.getValue();
        assertThat(attributes.getUsername()).isEqualTo(user.getUsername());
        assertThat(attributes.getUserPoolId()).isEqualTo(cognitoProps.getUserPoolId());

        assertContainsAttributeWithValue(attributes.getUserAttributes(), "email_verified", "true");
    }

    void assertUserIsCreated(RegisterApplicant applicantToCreate, String unformattedTelephoneNumber) {
        // make sure we actually send the request to create a user and capture the result of it
        verify(cognitoClient).adminCreateUser(adminUserRequestCaptor.capture());

        // test those values against the data we passed in
        AdminCreateUserRequest newUserRequest = adminUserRequestCaptor.getValue();

        assertThat(newUserRequest.getUserPoolId()).isEqualTo(cognitoProps.getUserPoolId());
        assertThat(newUserRequest.getUsername()).isEqualTo(applicantToCreate.getEmail());

        // check the data contained inside the custom attributes sent to Cognito
        final List<AttributeType> attributes = newUserRequest.getUserAttributes();
        assertContainsAttributeWithValue(attributes, "custom:features", "user=ordinary_user");
        assertContainsAttributeWithValue(attributes, "custom:isAdmin", "false");
        assertContainsAttributeWithValue(attributes, "custom:phoneNumber", "+44".concat(unformattedTelephoneNumber.substring(1)));
        assertContainsAttributeWithValue(attributes, "email", applicantToCreate.getEmail());
        assertContainsAttributeWithValue(attributes, "family_name", applicantToCreate.getLastName());
        assertContainsAttributeWithValue(attributes, "given_name", applicantToCreate.getFirstName());
        assertContainsAttributeWithValue(attributes, "custom:lastLogin", "1970-01-01T00:00:00Z");
    }

    private void assertContainsAttributeWithValue(List<AttributeType> attributes, String attributeName, String value) {
        attributes.stream()
                .filter(a -> a.getName().equals(attributeName))
                .findAny()
                .ifPresentOrElse(
                        a -> assertThat(a.getValue()).isEqualTo(value),
                        () -> Assertions.fail(String.format("No attribute with name '%s' found", attributeName))
                );
    }
}
