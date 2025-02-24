package org.example.gym.domain.training.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.utils.DurationEditor;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.service.TrainingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

import static org.example.gym.common.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(TRAININGS_BASE)
public class TrainingController {
    private final TrainingService trainingService;

    //    12. Get Trainee Trainings List (GET method)
    @GetMapping(GET_TRAINEE_TRAININGS)
    public List<TrainingDTO.Response.TrainingProfileForTrainee> getTraineeTrainings(@Valid @ModelAttribute TrainingDTO.Request.TraineeTrains dto) {
        log.info("GET request to " + GET_TRAINEE_TRAININGS + " for trainee with username={}, period from {} to {}, trainer={}",
                dto.getTraineeUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTrainerUsername());
        return trainingService.findTraineeList(dto.getTraineeUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTrainerUsername(),
                String.valueOf(dto.getTrainingType()));
    }

    //  13. Get Trainer Trainings List (GET method)
    @GetMapping(GET_TRAINER_TRAININGS)
    public List<TrainingDTO.Response.TrainingProfileForTrainer> getTrainerTrainings(@Valid @ModelAttribute TrainingDTO.Request.TrainerTrainings dto) {
        log.info("GET request to " + GET_TRAINER_TRAININGS + " for trainer with username={}, period from {} to {}, trainee={}",
                dto.getTrainerUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTraineeUsername());
        return trainingService.findTrainerList(dto.getTrainerUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTraineeUsername());
    }

    //    14. Add Training (POST method)
    @PostMapping(CREATE_TRAINING)
    public void createTraining(@Valid @RequestBody TrainingDTO.Request.NewTraining dto) {
        log.info("POST request to" + CREATE_TRAINING + " for creating training for trainee={} with details: {}, {}, {}, trainer name={}",
                dto.getTraineeUsername(), dto.getTrainingName(), dto.getTrainingDate(), dto.getTrainingDuration(), dto.getTrainerUsername());
        trainingService.create(dto.getTraineeUsername(), dto.getTrainerUsername(), dto.getTrainingName(), dto.getTrainingDate(), dto.getTrainingDuration());
    }

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.registerCustomEditor(Duration.class, new DurationEditor());
    }
}
