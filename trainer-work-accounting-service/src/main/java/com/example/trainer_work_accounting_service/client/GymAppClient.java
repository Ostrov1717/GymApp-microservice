package com.example.trainer_work_accounting_service.client;

import com.example.trainer_work_accounting_service.dto.AuthResponse;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "gym")
public interface GymAppClient {
    @GetMapping("/api/v1/trainings/all-trainings")
    List<TrainerTrainingDTO> getAllTrainings(@RequestHeader("Authorization") String bearerToken,
                                             @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId);

    @PostMapping("/api/v1/user/login")
    AuthResponse authenticate(@RequestHeader("Authorization") String basicAuth,
                              @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId);
}
