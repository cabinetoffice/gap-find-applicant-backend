package gov.cabinetoffice.gap.applybackend.validation.annotations;

import gov.cabinetoffice.gap.applybackend.validation.validators.AlphaCharacterValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AlphaCharacterValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainsOnlyAlphaChars {

    String message() default "Must only contain letters";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
