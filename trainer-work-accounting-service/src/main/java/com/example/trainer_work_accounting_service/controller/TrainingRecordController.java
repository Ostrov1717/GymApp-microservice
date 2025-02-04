package com.example.trainer_work_accounting_service.controller;

import com.example.trainer_work_accounting_service.service.TrainingDurationService;
import com.example.trainer_work_accounting_service.service.TrainingRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/trainings")
@Slf4j
@RequiredArgsConstructor
public class TrainingRecordController {
    private final TrainingRecordService service;

    private final TrainingDurationService durationService;

    @PostMapping
    public void addTraining(@RequestBody TrainerTrainingDTO record,  @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId) {
        log.info("POST request to microservice-/api/trainings for add training to trainer's {}",record.username());
        service.addTrainingRecord(record);
        durationService.addTraining(record);
    }

    @DeleteMapping
    public void deleteTraining(@RequestBody TrainerTrainingDTO record) {
        log.info("DELETE request to microservice-/api/trainings for delete training to trainer's {}",record.username());
        service.deleteTrainingRecord(record);
        durationService.removeTraining(record);
    }

    @GetMapping
    public TrainerWorkingHoursDTO getMonthlyHours(@RequestParam String trainerUsername, @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId) {
        log.info("GET request to microservice-/api/trainings for trainer's {} work hours",trainerUsername);
//        return service.calculateYearlyTrainingDuration(trainerUsername);
    return durationService.getTrainingSummaryByUsername(trainerUsername);
    }
}
