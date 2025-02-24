package org.example.gym.domain.training.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.messaging.MessageSenderService;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainee.repository.TraineeRepository;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.trainer.repository.TrainerRepository;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.dto.TrainingMapper;
import org.example.gym.domain.training.entity.Training;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.training.repository.TrainingRepository;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final MessageSenderService messageSenderService;


    @Transactional
    public void create(@NonNull String traineeUsername, @NonNull String trainerUsername, @NonNull String trainingName, @NonNull LocalDateTime trainingDate, @NonNull Duration duration) {
        log.info("Creation of new training: traineeId={}, trainerId={}, name={}, date={}, duration={}",
                traineeUsername, trainerUsername, trainingName, trainingDate, duration);
        if (trainingName.isBlank()) {
            log.error("Error: trainingName cannot be blank");
            throw new IllegalArgumentException("Training name cannot be null/blank");
        }
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee with username: " + traineeUsername + " not found."));
        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer with username: " + trainerUsername + " not found."));
        TrainingType trainingType = trainer.getSpecialization();
        Training training = new Training(trainee, trainer, trainingName, trainingType, trainingDate, duration);
        log.info("Training has been created: name={}, date={}", trainingName, trainingDate);
        log.info("Training record was transferred to microservice");
        trainingRepository.save(training);
        TrainerTrainingDTO dto = new TrainerTrainingDTO(trainerUsername, trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getUser().isActive(), trainingDate, duration, ActionType.ADD);
        messageSenderService.sendMessage(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE, dto);
    }

    @Transactional
    public List<TrainingDTO.Response.TrainingProfileForTrainer> findTrainerList(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        log.info("Search trainings for trainer: {}", trainerUsername);
        List<Training> trainings = trainingRepository.findTrainingsByTrainerAndCriteria(trainerUsername, fromDate, toDate, traineeName);
        log.info("Found {} trainings for trainer: {}", trainings.size(), trainerUsername);
        return TrainingMapper.toListForTrainer(trainings);
    }

    @Transactional
    public List<TrainingDTO.Response.TrainingProfileForTrainee> findTraineeList(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        log.info("Search trainings for trainee: {}", traineeUsername);
        List<Training> trainings = trainingRepository.findTrainingsByTraineeAndCriteria(traineeUsername, fromDate, toDate, trainerName);
        log.info("Found {} trainings for trainee: {}", trainings.size(), traineeUsername);
        return TrainingMapper.toListForTrainee(trainings);
    }

    @PostConstruct
    public void findAllTrainings() {
        List<TrainerTrainingDTO> dtoList = trainerRepository.findTrainerTrainings();
        dtoList.forEach(dto -> messageSenderService.sendMessage(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE, dto));
        log.info("Send all trainings to microservice for create DB, {} trainings", dtoList.size());
    }
}
