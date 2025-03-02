package org.example.gym.common.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LocalDateTimeRangeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeRange {
    String message() default "Date must be between {startYear} and {endYear}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int startYear();
    int endYear();
}