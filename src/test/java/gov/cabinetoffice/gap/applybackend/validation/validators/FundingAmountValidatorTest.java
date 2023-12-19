package gov.cabinetoffice.gap.applybackend.validation.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundingAmountValidatorTest {


    private ConstraintValidatorContext validatorContext;

    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @InjectMocks
    private FundingAmountValidator validatorUnderTest;

    private static Stream<Arguments> provideValidationData__invalid() {
        return Stream.of(

                Arguments.of("", "You must enter an answer"),   // Invalid, empty value
                Arguments.of("100.50", "Funding amount must only contain whole numbers"), // Invalid, contains a decimal point
                Arguments.of("100,50", "Funding amount must only contain whole numbers"), // Invalid, contains a comma
                Arguments.of("-50", "Funding amount must have a value greater than zero"), // Invalid, less than or equal to zero
                Arguments.of("0", "Funding amount must have a value greater than zero"), // Invalid, less than or equal to zero
                Arguments.of("0123456789012345", "Funding amount cannot start with zero"), // Invalid start with 0
                Arguments.of("12345678901234567", "Funding amount cannot be longer than 16 digits"), // Invalid, more than 16 digits
                Arguments.of("invalidValue", "Funding amount must only contain whole numbers") // Invalid, non-numeric value
        );
    }

    private static Stream<Arguments> provideValidationData__valid() {
        return Stream.of(
                Arguments.of("1234567890123456", true), // valid, less than 16 digits
                Arguments.of("100", true), // Valid, whole number
                Arguments.of("1", true) //Valid whole number
        );
    }

    @BeforeEach
    void setup() {
        validatorUnderTest = new FundingAmountValidator();
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        validatorContext = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @MethodSource("provideValidationData__invalid")
    void isValidReturnsExpectedResult(final String value, final String expectedMessage) {
        when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builder);
        boolean result = validatorUnderTest.isValid(value, validatorContext);
        verify(validatorContext).buildConstraintViolationWithTemplate(expectedMessage);
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideValidationData__valid")
    void isValidReturnsExpectedResult(final String value, final boolean expectedResult) {
        boolean result = validatorUnderTest.isValid(value, validatorContext);
        assertThat(result).isEqualTo(expectedResult);
    }
}