package org.example.gym.domain.messaging;

import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResponseStorageService {
    private final ConcurrentHashMap<String, CompletableFuture<TrainerWorkingHoursDTO>> responseMap = new ConcurrentHashMap<>();

    public void storeResponse(TrainerWorkingHoursDTO responseDTO) {
        CompletableFuture<TrainerWorkingHoursDTO> future = responseMap.remove(responseDTO.username());
        if (future != null) {
            future.complete(responseDTO);
        }
    }

    public CompletableFuture<TrainerWorkingHoursDTO> getResponseFuture(String username) {
        return responseMap.computeIfAbsent(username, key -> new CompletableFuture<>());
    }
}
