package gov.cabinetoffice.gap.applybackend.validation;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import gov.cabinetoffice.gap.applybackend.validation.validators.PhoneNumberValidator;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class PhoneNumberValidatorTest {
    private final PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator(PhoneNumberUtil.getInstance());
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void validatePhoneNumber_ReturnsExpectedResponse() {

        boolean methodResponse = phoneNumberValidator.isValid("07123456789", constraintValidatorContext);
        assertTrue(methodResponse);
    }

    @Test
    public void invalidPhoneNumber_ReturnsException() {

        boolean methodResponse = phoneNumberValidator.isValid("0716789", constraintValidatorContext);
        assertFalse(methodResponse);
    }

}
