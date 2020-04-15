package com.backend.tasks.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for NonWhiteSpace validation.
 */
@Documented
@Constraint(validatedBy = NonWhitespacesFieldValidator.class)
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonWhitespacesField {

    String message() default "Field shouldn't contain whitespaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
