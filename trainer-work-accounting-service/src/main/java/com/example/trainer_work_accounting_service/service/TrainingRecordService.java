package com.example.trainer_work_accounting_service.service;


import com.example.trainer_work_accounting_service.domain.TrainingRecord;
import com.example.trainer_work_accounting_service.repository.TrainingRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingRecordService {
    private final TrainingRecordRepository repository;

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
}
