package gov.cabinetoffice.gap.applybackend.validation;

import gov.cabinetoffice.gap.applybackend.model.RegisterApplicant;
import gov.cabinetoffice.gap.applybackend.validation.annotations.EmailAddressesMatch;
import gov.cabinetoffice.gap.applybackend.validation.validators.EmailAddressMatchMatchValidator;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EmailAddressMatchValidatorTest {
    private EmailAddressMatchMatchValidator emailAddressesMatchValidator;

    private ConstraintValidatorContext validatorContext;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setup() {
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        validatorContext = mock(ConstraintValidatorContext.class);

        emailAddressesMatchValidator = new EmailAddressMatchMatchValidator();
    }

    private static Stream<Arguments> provideRegistrationData() {
        return Stream.of(

                // validation only kicks in if both values are not blank
                Arguments.of(
                        RegisterApplicant.builder()
                                .email("")
                                .emailConfirmed("")
                                .build()
                ),
                Arguments.of(
                        RegisterApplicant.builder()
                                .email("email@test.com")
                                .emailConfirmed("")
                                .build()
                ),
                Arguments.of(
                        RegisterApplicant.builder()
                                .email("")
                                .emailConfirmed("email@test.com")
                                .build()
                ),

                // email addresses match
                Arguments.of(
                        RegisterApplicant.builder()
                                .email("email@test.com")
                                .emailConfirmed("email@test.com")
                                .build()
                )
        );
    }

    @MethodSource("provideRegistrationData")
    @ParameterizedTest
    void isValid_ReturnsTrueWhenExpected(RegisterApplicant registrationData) {
        final String emailField = "email";
        final String emailMatchField = "emailConfirmed";
        final Map<String, Object> map = Map.of("field", emailField, "fieldMatch", emailMatchField);
        final EmailAddressesMatch fieldsMatch = new AnnotationDescriptor.Builder<>(EmailAddressesMatch.class, map).build().getAnnotation();

        emailAddressesMatchValidator.initialize(fieldsMatch);
        boolean methodResponse = emailAddressesMatchValidator.isValid(registrationData, validatorContext);

        assertThat(methodResponse).isTrue();
    }

    @Test
    void isValid_ReturnsFalseWhenExpected() {
        final String message = "fields do not match";
        final String emailField = "email";
        final String emailMatchField = "emailConfirmed";
        final Map<String, Object> map = Map.of("field", emailField, "fieldMatch", emailMatchField, "message", message);
        final EmailAddressesMatch fieldsMatch = new AnnotationDescriptor.Builder<>(EmailAddressesMatch.class, map).build().getAnnotation();

        final RegisterApplicant registrationData = RegisterApplicant.builder()
                .email("an-email@test.com")
                .emailConfirmed("another-email@test.com")
                .build();

        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(validatorContext.getDefaultConstraintMessageTemplate())
                .thenReturn(message);
        when(builder.addPropertyNode(Mockito.anyString()))
                .thenReturn(nodeBuilder);

        emailAddressesMatchValidator.initialize(fieldsMatch);
        boolean methodResponse = emailAddressesMatchValidator.isValid(registrationData, validatorContext);

        verify(validatorContext, atLeastOnce()).buildConstraintViolationWithTemplate(message);
        verify(builder, atLeastOnce()).addPropertyNode(emailField);
        verify(builder, atLeastOnce()).addPropertyNode(emailMatchField);

        assertThat(methodResponse).isFalse();
    }
}
