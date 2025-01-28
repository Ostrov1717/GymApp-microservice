package com.example.trainer_work_accounting_service.controller;

import com.example.trainer_work_accounting_service.service.TrainingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/trainings")
@Slf4j
public class TrainingRecordController {
    private final TrainingRecordService service;

    public TrainingRecordController(TrainingRecordService service) {
        this.service = service;
    }

    @PostMapping
    public void addTraining(@RequestBody TrainerTrainingDTO record,  @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId) {
        log.info("POST request to microservice-/api/trainings for add training to trainer's {}",record.username());
        service.addTrainingRecord(record);
    }

    @DeleteMapping
    public void deleteTraining(@RequestBody TrainerTrainingDTO record) {
        log.info("DELETE request to microservice-/api/trainings for delete training to trainer's {}",record.username());
        service.deleteTrainingRecord(record);
    }

    @GetMapping
    public TrainerWorkingHoursDTO getMonthlyHours(@RequestParam String trainerUsername, @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId) {
        log.info("GET request to microservice-/api/trainings for trainer's {} work hours",trainerUsername);
        return service.calculateYearlyTrainingDuration(trainerUsername);
    }
}
