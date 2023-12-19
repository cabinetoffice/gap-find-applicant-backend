package gov.cabinetoffice.gap.applybackend.validation.validators;

import gov.cabinetoffice.gap.applybackend.validation.annotations.ValidFundingAmount;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class FundingAmountValidator implements ConstraintValidator<ValidFundingAmount, String> {
    @Override
    public void initialize(ValidFundingAmount constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {


        try {
            if (value.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("You must enter an answer").addConstraintViolation();
                return false;
            } else if (value.contains(".") || value.contains(",")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Funding amount must only contain whole numbers").addConstraintViolation();
                return false;
            } else if (new BigDecimal(value).compareTo(BigDecimal.ZERO) <= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Funding amount must have a value greater than zero").addConstraintViolation();
                return false;
            } else if (value.startsWith("0")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Funding amount cannot start with zero").addConstraintViolation();
                return false;
            } else if (value.length() > 16) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Funding amount cannot be longer than 16 digits").addConstraintViolation();
                return false;
            }

        } catch (final NumberFormatException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Funding amount must only contain whole numbers").addConstraintViolation();
            return false;
        }

        return true;
    }
}