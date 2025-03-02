package org.example.gym.common.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class DurationMinValidator implements ConstraintValidator<MinDuration, Duration> {

    private long minSeconds;

    @Override
    public void initialize(MinDuration constraintAnnotation) {
        this.minSeconds = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return duration != null && duration.getSeconds() >= minSeconds;
    }
}