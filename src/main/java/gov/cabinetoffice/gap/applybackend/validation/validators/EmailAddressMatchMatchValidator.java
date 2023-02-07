package gov.cabinetoffice.gap.applybackend.validation.validators;


import gov.cabinetoffice.gap.applybackend.validation.annotations.EmailAddressesMatch;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailAddressMatchMatchValidator implements ConstraintValidator<EmailAddressesMatch, Object> {


    private String field;
    private String fieldMatch;


    @Override
    public void initialize(EmailAddressesMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(fieldMatch);

        final boolean fieldValueIsEmpty = Strings.isEmpty((String) fieldValue);
        final boolean fieldMatchValueIsEmpty = Strings.isEmpty((String) fieldMatchValue);
        final boolean fieldsMatch = fieldValue.equals(fieldMatchValue);

        if ((!fieldValueIsEmpty && !fieldMatchValueIsEmpty) && !fieldsMatch) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(field).addConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(fieldMatch).addConstraintViolation();
            return false;
        }

        return true;
    }
}
