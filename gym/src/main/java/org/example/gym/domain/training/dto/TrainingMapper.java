package org.example.gym.domain.training.dto;

import lombok.extern.slf4j.Slf4j;
import org.example.gym.domain.training.entity.Training;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TrainingMapper {
    public static TrainingDTO.Response.TrainingProfileForTrainee toProfileForTrainee(Training training) {
        if (training == null) {
            log.warn("Provided Training object is null, returning null.");
            throw new IllegalArgumentException("Provided in mapper training object is null, returning null.");
        }
        log.debug("Mapping Training [{}] to TrainingProfile.", training);
        TrainingDTO.Response.TrainingProfileForTrainee trainingProfile = new TrainingDTO.Response.TrainingProfileForTrainee(
                training.getTrainingName(), training.getTrainingDate(), training.getTrainingType(), training.getTrainingDuration(),
                training.getTrainer().getUser().getUsername());
        log.debug("Mapped basic training info: training name [{}], trainer's name [{}], date [{}].",
                trainingProfile.getTrainingName(), training.getTrainer(), training.getTrainingDate());
        return trainingProfile;
    }

    public static List<TrainingDTO.Response.TrainingProfileForTrainee> toListForTrainee(List<Training> trainings) {
        List<TrainingDTO.Response.TrainingProfileForTrainee> trainingList = new ArrayList<>();
        for (Training training : trainings) {
            trainingList.add(toProfileForTrainee(training));
        }
        return trainingList;
    }

    public static TrainingDTO.Response.TrainingProfileForTrainer toProfileForTrainer(Training training) {
        if (training == null) {
            log.warn("Provided Training object is null, returning null.");
            throw new IllegalArgumentException("Provided in mapper training object is null, returning null.");
        }
        log.debug("Mapping Training [{}] to TrainingProfile.", training);
        TrainingDTO.Response.TrainingProfileForTrainer trainingProfile = new TrainingDTO.Response.TrainingProfileForTrainer(
                training.getTrainingName(), training.getTrainingDate(), training.getTrainingType(), training.getTrainingDuration(),
                training.getTrainee().getUser().getUsername());
        log.debug("Mapped basic training info: training name [{}], trainer's name [{}], date [{}].",
                trainingProfile.getTrainingName(), training.getTrainer(), training.getTrainingDate());
        return trainingProfile;
    }

    public static List<TrainingDTO.Response.TrainingProfileForTrainer> toListForTrainer(List<Training> trainings) {
        List<TrainingDTO.Response.TrainingProfileForTrainer> trainingList = new ArrayList<>();
        for (Training training : trainings) {
            trainingList.add(toProfileForTrainer(training));
        }
        return trainingList;
    }
}
