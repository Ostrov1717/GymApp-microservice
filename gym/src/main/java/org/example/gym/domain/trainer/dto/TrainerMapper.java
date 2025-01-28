package org.example.gym.domain.trainer.dto;

import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainer.entity.Trainer;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class TrainerMapper {

    public static TrainerDTO.Response.TrainerProfile toProfile(Trainer trainer) {
        if (trainer == null) {
            log.warn("Provided Trainer object is null, returning null.");
            throw new UserNotFoundException("Provided in mapper Trainer object is null, returning null.");
        }
        log.debug("Mapping Trainer [{}] to TrainerProfile.", trainer.getUser().getUsername());
        Set<TraineeDTO.Response.TraineeSummury> trainees = new HashSet<>();
        for (Trainee trainee : trainer.getTrainees()) {
            trainees.add(new TraineeDTO.Response.TraineeSummury(trainee.getUser().getUsername(), trainee.getUser().getFirstName(), trainee.getUser().getLastName()));
        }
        TrainerDTO.Response.TrainerProfile trainerProfile = new TrainerDTO.Response.TrainerProfile(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getSpecialization(), trainer.getUser().isActive(), trainees);
        return trainerProfile;
    }

    public static Set<TrainerDTO.Response.TrainerSummury> toSetTrainerSummury(Set<Trainer> trainers) {
        if (trainers == null) {
            log.warn("Provided Trainers set is null, returning null.");
            throw new UserNotFoundException("Provided in mapper Trainer object is null, returning null.");
        }
        Set<TrainerDTO.Response.TrainerSummury> trainerSummurySet = new HashSet<>();
        for (Trainer trainer : trainers) {
            trainerSummurySet.add(new TrainerDTO.Response.TrainerSummury(trainer.getUser().getUsername(),
                    trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization()));
        }
        return trainerSummurySet;
    }
}
