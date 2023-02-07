package gov.cabinetoffice.gap.applybackend.validation.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import gov.cabinetoffice.gap.applybackend.validation.annotations.PhoneNumberIsValid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
@RequiredArgsConstructor
public class PhoneNumberValidator
        implements ConstraintValidator<PhoneNumberIsValid, String> {
    final PhoneNumberUtil phoneNumberUtil;

    @Override
    public boolean isValid(String telephoneField,
                           ConstraintValidatorContext cxt) {
        boolean phoneNumberIsValid = false;

        try {
            Phonenumber.PhoneNumber ukNumberProto = phoneNumberUtil.parse(telephoneField, "GB");
            phoneNumberIsValid = phoneNumberUtil.isValidNumber(ukNumberProto);
        } catch (NumberParseException e) {
            log.debug(String.format("Invalid phone number: %s", telephoneField), e);
        }

        return phoneNumberIsValid;
    }
}