package com.example.trainer_work_accounting_service.client;

import com.example.trainer_work_accounting_service.dto.AuthResponse;
import org.example.shareddto.TrainerTrainingDTO;

import java.util.ArrayList;
import java.util.List;

public class GymAppClientFallback implements GymAppClient{
    @Override
    public List<TrainerTrainingDTO> getAllTrainings(String bearerToken, String transactionId) {
        return new ArrayList<>();
    }

    @Override
    public AuthResponse authenticate(String basicAuth, String transactionId) {
        return new AuthResponse("Service for autentication no available","");
    }
}