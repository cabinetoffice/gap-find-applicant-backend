package gov.cabinetoffice.gap.applybackend.validation.annotations;


import gov.cabinetoffice.gap.applybackend.validation.validators.FundingAmountValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FundingAmountValidator.class)
public @interface ValidFundingAmount {
    String message() default "Invalid funding amount";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}