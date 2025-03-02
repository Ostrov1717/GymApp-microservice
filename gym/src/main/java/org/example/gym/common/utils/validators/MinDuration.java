package org.example.gym.common.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DurationMinValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinDuration {
    String message() default "Duration must be at least {value} seconds";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long value();
}