package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.client.GymAppClient;
import com.example.trainer_work_accounting_service.dto.AuthResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSyncService {
    private final GymAppClient gymAppClient;
    private final TrainingRecordService service;
    private final TrainingDurationService durationService;
    @Value("${app.security.password}")
    String CREDENTIALS;

    @PostConstruct
    public void syncDataOnStartup() {
        String encodedCredentials = Base64.getEncoder().encodeToString(CREDENTIALS.getBytes(StandardCharsets.UTF_8));
        String transactionId = java.util.UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        try {
            AuthResponse response = gymAppClient.authenticate("Basic " + encodedCredentials, transactionId);
            List<TrainerTrainingDTO> dataList = gymAppClient.getAllTrainings("Bearer " + response.token(), transactionId);
            for (TrainerTrainingDTO dto : dataList) {
                service.addTrainingRecord(dto);
                durationService.addTraining(dto);
            }
            log.info("Data synchronized successfully.");
        } catch (Exception e) {
            log.info("Data sync error: " + e.getMessage());
        }
    }
}
