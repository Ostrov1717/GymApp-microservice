package org.example.shareddto;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Map;

public record TrainerWorkingHoursDTO(
        String username,

        String firstName,

        String lastName,

        boolean active,

        Map<Year, Map<Month, Duration>> trainingsDuration
) {
}
