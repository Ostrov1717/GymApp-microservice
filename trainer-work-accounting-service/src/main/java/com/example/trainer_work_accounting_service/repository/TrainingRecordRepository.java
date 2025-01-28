package com.example.trainer_work_accounting_service.repository;

import com.example.trainer_work_accounting_service.domain.TrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRecordRepository extends JpaRepository<TrainingRecord,Long> {
    Optional<TrainingRecord> findByUsernameAndFirstNameAndLastNameAndTrainingDateAndTrainingDuration(
            String username,
            String firstName,
            String lastName,
            LocalDateTime trainingDate,
            Duration trainingDuration
    );
    List<TrainingRecord> findByUsername(String username);
}
