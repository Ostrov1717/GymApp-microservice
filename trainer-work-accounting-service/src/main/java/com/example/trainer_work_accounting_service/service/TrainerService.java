package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.messaging.MessageSenderService;
import com.example.trainer_work_accounting_service.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.*;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final MessageSenderService messageSenderService;

    @Transactional
    public void addTraining(TrainerTrainingDTO trainingDTO) {
        Year trainingYear = Year.of(trainingDTO.trainingDate().getYear());
        Month trainingMonth = trainingDTO.trainingDate().getMonth();

        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(trainingDTO.username());
        if (trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            Trainer.YearSummary yearSummary = findOrCreateYearSummary(trainer, trainingYear);
            Trainer.MonthSummary monthSummary = findOrCreateMonthSummary(yearSummary, trainingMonth);
            monthSummary.setDuration(monthSummary.getDuration() + (trainingDTO.trainingDuration()).getSeconds());
            trainerRepository.save(trainer);
            log.info("The trainer's {} training data has been updated. + {} duration", trainingDTO.username(), trainingDTO.trainingDuration());
        } else {
            Trainer trainer = new Trainer(
                    null,
                    trainingDTO.username(),
                    trainingDTO.firstName(),
                    trainingDTO.lastName(),
                    trainingDTO.active(),
                    List.of(new Trainer.YearSummary(trainingYear.getValue(),
                            List.of(new Trainer.MonthSummary(trainingMonth.toString(),
                                    trainingDTO.trainingDuration().getSeconds()))))
            );
            trainerRepository.save(trainer);
            log.info("The trainer's {} training data has been added. + {} duration", trainingDTO.username(), trainingDTO.trainingDuration());
        }
    }

    @Transactional
    public void deleteTraining(TrainerTrainingDTO trainingDTO) {
        Year trainingYear = Year.of(trainingDTO.trainingDate().getYear());
        Month trainingMonth = trainingDTO.trainingDate().getMonth();

        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(trainingDTO.username());
        if (trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            Trainer.YearSummary yearSummary = findOrCreateYearSummary(trainer, trainingYear);
            Trainer.MonthSummary monthSummary = findOrCreateMonthSummary(yearSummary, trainingMonth);
            long newDuration = monthSummary.getDuration() - trainingDTO.trainingDuration().getSeconds();
            if (newDuration < 0) {
                newDuration = 0;
            }
            monthSummary.setDuration(newDuration);
            log.info("The trainer's {} training data has been deleted. - {} duration.", trainingDTO.username(), trainingDTO.trainingDuration());
            trainerRepository.save(trainer);
        } else {
            throw new IllegalArgumentException("Trainer not found for username: " + trainingDTO.username());
        }
    }

    private Trainer.YearSummary findOrCreateYearSummary(Trainer trainer, Year year) {
        return trainer.getYearSummaries().stream()
                .filter(yearSummary -> yearSummary.getYear() == year.getValue())
                .findFirst()
                .orElseGet(() -> {
                    Trainer.YearSummary newYearSummary = new Trainer.YearSummary(year.getValue(), new ArrayList<>());
                    trainer.getYearSummaries().add(newYearSummary);
                    return newYearSummary;
                });
    }

    private Trainer.MonthSummary findOrCreateMonthSummary(Trainer.YearSummary yearSummary, Month month) {
        return yearSummary.getMonthDurationList().stream()
                .filter(monthSummary -> monthSummary.getMonth().equals(month.toString()))
                .findFirst()
                .orElseGet(() -> {
                    Trainer.MonthSummary newMonthSummary = new Trainer.MonthSummary(month.toString(), 0);
                    yearSummary.getMonthDurationList().add(newMonthSummary);
                    return newMonthSummary;
                });
    }

    public void getTrainingSummaryByUsername(String username) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        if (trainerOptional.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found for username: " + username);
        }
        Trainer trainer = trainerOptional.get();
        Map<Year, Map<Month, Duration>> trainingsDuration = new TreeMap<>();
        for (Trainer.YearSummary yearSummary : trainer.getYearSummaries()) {
            Map<Month, Duration> monthToDurationMap = new TreeMap<>();
            for (Trainer.MonthSummary monthSummary : yearSummary.getMonthDurationList()) {
                monthToDurationMap.put(Month.valueOf(monthSummary.getMonth()), Duration.ofSeconds(monthSummary.getDuration()));
            }
            trainingsDuration.put(Year.of(yearSummary.getYear()), monthToDurationMap);
        }
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                trainingsDuration
        );
        messageSenderService.sendMessage(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN, dto);
    }
}