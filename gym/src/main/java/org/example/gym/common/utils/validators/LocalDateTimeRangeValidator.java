package org.example.gym.common.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class LocalDateTimeRangeValidator implements ConstraintValidator<DateTimeRange, LocalDateTime> {
    private int startYear;
    private int endYear;

    @Override
    public void initialize(DateTimeRange constraintAnnotation) {
        this.startYear = constraintAnnotation.startYear();
        this.endYear = constraintAnnotation.endYear();
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return localDateTime != null &&
               localDateTime.getYear() >= startYear &&
               localDateTime.getYear() <= endYear;
    }
}