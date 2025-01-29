package org.example.gym.domain.trainer.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.gym.common.exception.ServiceUnavailableException;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static org.example.gym.common.ApiUrls.CLIENT_URL;

@FeignClient(name = "trainer-work-accounting-service")
public interface ServiceTrainerWorkingHours {
    @GetMapping(CLIENT_URL)
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackGetData")
    TrainerWorkingHoursDTO getMonthlyHours(@RequestParam("trainerUsername") String trainerUsername,
                                           @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId);

    @PostMapping(CLIENT_URL)
    @CircuitBreaker(name = "myCircuitBreaker")
    void addTraining(@RequestBody TrainerTrainingDTO record,
                     @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId);

    default TrainerWorkingHoursDTO fallbackGetData(String trainerUsername, String transactionId, Throwable throwable) {
        throw new ServiceUnavailableException("Microservice error occurred " + throwable.getClass().getSimpleName());
    }
}