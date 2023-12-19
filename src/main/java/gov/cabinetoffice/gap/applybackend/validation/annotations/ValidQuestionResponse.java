package gov.cabinetoffice.gap.applybackend.validation.annotations;

import gov.cabinetoffice.gap.applybackend.validation.validators.QuestionResponseValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = { QuestionResponseValidator.class })
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidQuestionResponse {
    String message() default "Invalid value";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
