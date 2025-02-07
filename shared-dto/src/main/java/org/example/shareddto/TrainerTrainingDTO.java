package org.example.shareddto;

import java.time.Duration;
import java.time.LocalDateTime;

public record TrainerTrainingDTO (
    String username,
    String firstName,
    String lastName,
    boolean active,
    LocalDateTime trainingDate,
    Duration trainingDuration,
    ActionType action
){
    public TrainerTrainingDTO(String username, String firstName, String lastName, boolean active,
                              LocalDateTime trainingDate, Duration trainingDuration, String actionType) {
        this(username, firstName, lastName, active, trainingDate, trainingDuration, ActionType.valueOf(actionType));
    }
}
