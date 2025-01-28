package org.example.shareddto;

import java.time.Duration;
import java.time.LocalDateTime;

public record TrainerTrainingDTO (
    String username,
    String firstName,
    String lastName,
    boolean active,
    LocalDateTime trainingDate,
    Duration trainingDuration
){
    public TrainerTrainingDTO(String username, String firstName, String lastName, boolean active, LocalDateTime trainingDate, Duration trainingDuration) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
