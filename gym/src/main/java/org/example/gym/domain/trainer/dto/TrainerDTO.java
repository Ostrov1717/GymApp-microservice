package org.example.gym.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.training.entity.TrainingType;

import java.util.Set;

@Schema(description = "Enum DTO contains details about the Trainer's RequestDTO and ResponseDTO")
public enum TrainerDTO {
    ;

    private interface Username {
        String getUsername();
    }

    private interface FirstName {
        String getFirstName();
    }

    private interface LastName {
        String getLastName();
    }

    private interface Specialization {
        TrainingType getSpecialization();
    }

    private interface Active {
        boolean isActive();
    }

    private interface Trainees {
        Set<TraineeDTO.Response.TraineeSummury> getTrainees();
    }

    public enum Request {
        ;

        @Data
        @AllArgsConstructor
        @Schema(description = "Request DTO contains details about the Trainer")
        public static class TrainerRegistration implements FirstName, LastName, Specialization {
            @NotBlank(message = "{first.name.required}")
            @Schema(description = "First name of the Trainer", example = "Monica")
            String firstName;
            @NotBlank(message = "{last.name.required}")
            @Schema(description = "Last name of the Trainee", example = "Dobs")
            String lastName;
            @NotNull(message = "{trainer.specification.required}")
            @Schema(description = "Training type specialization of the Trainer", example = "YOGA")
            TrainingType specialization;
        }

        @Data
        @AllArgsConstructor
        @Schema(description = "Request DTO contains details about the Trainer")
        public static class TrainerUpdate implements FirstName, LastName, Specialization, Active {
            @NotBlank(message = "{first.name.required}")
            @Schema(description = "First name of the Trainer", example = "Monica")
            String firstName;
            @NotBlank(message = "{last.name.required}")
            @Schema(description = "Last name of the Trainee", example = "Dobs")
            String lastName;
            @NotNull(message = "{trainer.specification.required}")
            @Schema(description = "Training type specialization of the Trainer", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType specialization;
            @NotNull(message = "{activity.status.required}")
            @Schema(description = "Activity status of the Trainer", example = "true")
            boolean active;
        }
    }

    public enum Response {
        ;

        @Data
        @AllArgsConstructor
        @Schema(description = "Response DTO contains details about the Trainer(profile)")
        public static class TrainerProfile implements FirstName, LastName, Specialization, Trainees {
            @Schema(description = "First name of the Trainer", example = "Monica")
            String firstName;
            @Schema(description = "Last name of the Trainee", example = "Dobs")
            String lastName;
            @Schema(description = "Training type specialization of the Trainer", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType specialization;
            @Schema(description = "Activity status of the Trainer", example = "true")
            boolean active;
            Set<TraineeDTO.Response.TraineeSummury> trainees;
        }

        @Data
        @AllArgsConstructor
        @Schema(description = "Response DTO contains details about the Trainer (summary info)")
        public static class TrainerSummury implements Username, FirstName, LastName, Specialization {
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String username;
            @Schema(description = "First name of the Trainer", example = "Monica")
            String firstName;
            @Schema(description = "Last name of the Trainee", example = "Dobs")
            String lastName;
            @Schema(description = "Training type specialization of the Trainer", example = "{\"id\": 1, \"trainingType\": \"YOGA\"}")
            TrainingType specialization;
        }

        @Data
        @AllArgsConstructor
        @Schema(description = "Response DTO contains details about the Trainer(only username)")
        public static class TrainerUsername implements Username {
            @Schema(description = "Username of the Trainer", example = "Monica.Dobs")
            String username;
        }
    }
}
