package org.example.gym.domain.messaging;

import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ResponseStorageService {
    private final ConcurrentHashMap<String, CompletableFuture<TrainerWorkingHoursDTO>> responseMap = new ConcurrentHashMap<>();

    public void storeResponse(TrainerWorkingHoursDTO responseDTO) {
        CompletableFuture<TrainerWorkingHoursDTO> future = responseMap.remove(responseDTO.username());
        if (future != null) {
           log.info("Completing future: " + future);
           future.complete(responseDTO);
           log.info("Completed future: " + future);
        }
    }

    public CompletableFuture<TrainerWorkingHoursDTO> getResponseFuture(String username) {
        CompletableFuture<TrainerWorkingHoursDTO> future = responseMap.computeIfAbsent(username, key -> {
            log.info("Creating new CompletableFuture for username: " + username);
            return new CompletableFuture<>();
        });
        log.info("Returning stored future: " + future);
        return future;
    }

    public void storeResponseFuture(String username, CompletableFuture<TrainerWorkingHoursDTO> responseFuture) {
        log.info("Storing CompletableFuture for username: " + username);
        responseMap.put(username, responseFuture);
    }
}
