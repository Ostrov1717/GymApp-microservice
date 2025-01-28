package org.example.gym.domain.training.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.gym.domain.training.entity.TrainingType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;

@Schema(description = "Request enum DTO contains details about trainings")
public enum TrainingDTO {
    ;

    private interface TraineeName {
        String getTraineeUsername();
    }

    private interface Password {
        String getPassword();
    }

    private interface PeriodFrom {
        LocalDateTime getPeriodFrom();
    }

    private interface PeriodTo {
        LocalDateTime getPeriodTo();
    }

    private interface TrainerName {
        String getTrainerUsername();
    }

    private interface TrainingSpecial {
        TrainingType getTrainingType();
    }

    private interface TrainingName {
        String getTrainingName();
    }

    private interface TrainingDate {
        LocalDateTime getTrainingDate();
    }

    private interface TrainingDuration {
        Duration getTrainingDuration();
    }

    @Schema(description = "Enum DTO contains details about Training RequestDTOs")
    public enum Request {
        ;

        @Data
        @AllArgsConstructor
        @Schema(description = "Request DTO contains details for creation trainings")
        public static class NewTraining implements TraineeName, TrainerName, TrainingName, TrainingDate, TrainingDuration {
            @NotBlank(message = "Trainee username is required")
            @Schema(description = "Username of the Trainee", example = "Olga.Kurilenko")
            String traineeUsername;
            @NotBlank(message = "Trainer username is required")
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String trainerUsername;
            @NotBlank(message = "Training name is required")
            @Schema(description = "Training name", example = "Yoga evening class")
            String trainingName;
            @NotNull(message = "Training date is required")
            @Schema(description = "Date and time of training", example = "1990-01-02T19:00")
            LocalDateTime trainingDate;
            @NotNull(message = "Training duration is required")
            @Schema(description = "Duration of training", example = "PT1H")
            Duration trainingDuration;
        }

        @Data
        @Schema(description = "Request DTO contains details about trainings for trainee")
        public static class TraineeTrains implements TraineeName, PeriodFrom, PeriodTo, TrainerName, TrainingSpecial {
            @NotBlank(message = "Trainee username is required")
            @Schema(description = "Username of the Trainee", example = "Olga.Kurilenko")
            String traineeUsername;
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Schema(description = "Date and time of training from", example = "2024-10-02T19:00")
            LocalDateTime periodFrom;
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Schema(description = "Date and time of training to", example = "2024-10-30T19:00:00")
            LocalDateTime periodTo;
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String trainerUsername;
            @Schema(description = "Training type of training", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType trainingType;
        }

        @Data
        @Schema(description = "Request DTO contains details about trainings for trainer")
        public static class TrainerTrainings implements TrainerName, PeriodFrom, PeriodTo, TraineeName, TrainingSpecial {
            @NotBlank(message = "Trainer username is required")
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String trainerUsername;
            @Schema(description = "Date and time of training from", example = "2024-10-02T19:00")
            LocalDateTime periodFrom;
            @Schema(description = "Date and time of training to", example = "2024-10-30T19:00")
            LocalDateTime periodTo;
            @Schema(description = "Username of the Trainee", example = "Olga.Kurilenko")
            String traineeUsername;
            @Schema(description = "Training type of training", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType trainingType;
        }
    }

    public enum Response {
        ;

        @Data
        @AllArgsConstructor
        public static class TrainingProfileForTrainee implements TrainingName, TrainingDate, TrainingSpecial, TrainingDuration, TrainerName {
            @Schema(description = "Username of the Trainee", example = "Olga.Kurilenko")
            String trainingName;
            @Schema(description = "Date and time of training", example = "2024-10-02T19:00")
            LocalDateTime trainingDate;
            @Schema(description = "Training type of training", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType trainingType;
            @Schema(description = "Duration of training", example = "PT1H")
            Duration trainingDuration;
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String trainerUsername;
        }

        @Data
        @AllArgsConstructor
        public static class TrainingProfileForTrainer implements TrainingName, TrainingDate, TrainingSpecial, TrainingDuration, TraineeName {
            @Schema(description = "Training name", example = "Yoga morning class")
            String trainingName;
            @Schema(description = "Date and time of training", example = "2024-10-02T19:00")
            LocalDateTime trainingDate;
            @Schema(description = "Training type of training", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType trainingType;
            @Schema(description = "Duration of training", example = "PT1H")
            Duration trainingDuration;
            @Schema(description = "Username of the Trainee", example = "Olga.Kurilenko")
            String traineeUsername;
        }
    }
}
