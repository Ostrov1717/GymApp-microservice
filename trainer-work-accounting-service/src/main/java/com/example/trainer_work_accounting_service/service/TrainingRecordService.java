package com.example.trainer_work_accounting_service.service;


import com.example.trainer_work_accounting_service.domain.TrainingRecord;
import com.example.trainer_work_accounting_service.repository.TrainingRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainingRecordService {
    private final TrainingRecordRepository repository;

    public TrainingRecordService(TrainingRecordRepository repository) {
        this.repository = repository;
    }

    public void addTrainingRecord(TrainerTrainingDTO record) {
        TrainingRecord training = new TrainingRecord();
        training.setUsername(record.username());
        training.setFirstName(record.firstName());
        training.setLastName(record.lastName());
        training.setActive(record.active());
        training.setTrainingDate(record.trainingDate());
        training.setTrainingDuration(record.trainingDuration());
        log.info("Training has been added: trainer's name={}, date={}", record.username(), record.trainingDate());
        repository.save(training);
    }

    public void deleteTrainingRecord(TrainerTrainingDTO record) {
        Optional<TrainingRecord> training =
                repository.findByUsernameAndFirstNameAndLastNameAndTrainingDateAndTrainingDuration(record.username(), record.firstName(),
                        record.lastName(), record.trainingDate(), record.trainingDuration());
        training.ifPresent(repository::delete);
    }

    public TrainerWorkingHoursDTO calculateYearlyTrainingDuration(String username) {
        List<TrainingRecord> trainings = repository.findByUsername(username);
        Map<Year, Map<Month, Duration>> map = repository.findByUsername(username)
                .stream()
                .collect(Collectors.groupingBy(
                        record -> Year.of(record.getTrainingDate().getYear()),
                        Collectors.groupingBy(
                                record -> record.getTrainingDate().getMonth(),
                                Collectors.reducing(
                                        Duration.ZERO,
                                        TrainingRecord::getTrainingDuration,
                                        Duration::plus
                                )
                        )
                ));
        log.info("Information about trainer's {} working hours was received", username);
        return new TrainerWorkingHoursDTO(trainings.get(0).getUsername(), trainings.get(0).getFirstName(), trainings.get(0).getLastName(),
                trainings.get(0).isActive(), map);
    }
}
