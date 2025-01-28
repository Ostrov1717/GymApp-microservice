package org.example.gym.domain.trainee.dto;

import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.entity.Trainer;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class TraineeMapper {
    public static TraineeDTO.Response.TraineeProfile toProfile(Trainee trainee) {
        if (trainee == null) {
            log.warn("Provided Trainee object is null, returning null.");
            throw new UserNotFoundException("Provided in mapper Trainee object is null, returning null.");
        }
        log.debug("Mapping Trainee [{}] to TraineeProfile.", trainee.getUser().getUsername());
        Set<TrainerDTO.Response.TrainerSummury> trainers = new HashSet<>();
        for (Trainer trainer : trainee.getTrainers()) {
            trainers.add(new TrainerDTO.Response.TrainerSummury(trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(), trainer.getUser().getUsername(), trainer.getSpecialization()));
        }
        TraineeDTO.Response.TraineeProfile traineeProfile = new TraineeDTO.Response.TraineeProfile(trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(), trainee.getDateOfBirth(), trainee.getAddress(), trainee.getUser().isActive(), trainers);
        return traineeProfile;
    }

    public static TraineeDTO.Response.TraineeProfileFull toProfileFull(Trainee trainee) {
        if (trainee == null) {
            log.warn("Provided Trainee object is null, returning null.");
            throw new UserNotFoundException("Provided in mapper Trainee object is null, returning null.");
        }
        log.debug("Mapping Trainee [{}] to TraineeProfile.", trainee.getUser().getUsername());
        Set<TrainerDTO.Response.TrainerSummury> trainers = new HashSet<>();
        for (Trainer trainer : trainee.getTrainers()) {
            trainers.add(new TrainerDTO.Response.TrainerSummury(trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(), trainer.getUser().getUsername(), trainer.getSpecialization()));
        }
        TraineeDTO.Response.TraineeProfileFull traineeProfile = new TraineeDTO.Response.TraineeProfileFull(trainee.getUser().getUsername(), trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(), trainee.getDateOfBirth(), trainee.getAddress(), trainee.getUser().isActive(), trainers);
        log.debug("Mapped basic user info: firstName [{}], lastName [{}], active [{}].",
                traineeProfile.getFirstName(), traineeProfile.getLastName(),
                traineeProfile.isActive());
        return traineeProfile;
    }
}
