package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.domain.MonthSummary;
import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.domain.YearSummary;
import com.example.trainer_work_accounting_service.messaging.MessageSenderService;
import com.example.trainer_work_accounting_service.repository.MonthSummaryRepository;
import com.example.trainer_work_accounting_service.repository.TrainerRepository;
import com.example.trainer_work_accounting_service.repository.YearSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingDurationService {
    private final TrainerRepository trainerRepository;
    private final YearSummaryRepository yearSummaryRepository;
    private final MonthSummaryRepository monthSummaryRepository;
    private final MessageSenderService messageSenderService;

    @Transactional
    public void addTraining(TrainerTrainingDTO record) {
        Trainer trainer = trainerRepository.findByUsername(record.username())
                .orElseGet(() -> {
                    Trainer newTrainer = new Trainer();
                    newTrainer.setUsername(record.username());
                    newTrainer.setFirstName(record.firstName());
                    newTrainer.setLastName(record.lastName());
                    newTrainer.setActive(record.active());
                    return trainerRepository.save(newTrainer);
                });

        Year trainingYear = Year.of(record.trainingDate().getYear());
        Month trainingMonth = record.trainingDate().getMonth();

        YearSummary yearSummary = yearSummaryRepository
                .findByTrainerAndYear(trainer, trainingYear)
                .orElseGet(() -> {
                    YearSummary newYearSummary = new YearSummary();
                    newYearSummary.setTrainer(trainer);
                    newYearSummary.setYear(trainingYear);
                    return yearSummaryRepository.save(newYearSummary);
                });

        MonthSummary monthSummary = monthSummaryRepository
                .findByYearSummaryAndMonth(yearSummary, trainingMonth)
                .orElseGet(() -> {
                    MonthSummary newMonthSummary = new MonthSummary();
                    newMonthSummary.setYearSummary(yearSummary);
                    newMonthSummary.setMonth(trainingMonth);
                    newMonthSummary.setDuration(Duration.ZERO);
                    return monthSummaryRepository.save(newMonthSummary);
                });

        monthSummary.setDuration(monthSummary.getDuration().plus(record.trainingDuration()));
        monthSummaryRepository.save(monthSummary);
    }

    @Transactional
    public void removeTraining(TrainerTrainingDTO record) {
        Trainer trainer = trainerRepository.findByUsername(record.username())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with username: " + record.username()));

        Year trainingYear = Year.of(record.trainingDate().getYear());
        Month trainingMonth = record.trainingDate().getMonth();

        YearSummary yearSummary = yearSummaryRepository.findByTrainerAndYear(trainer, trainingYear)
                .orElseThrow(() -> new EntityNotFoundException("No training data found for year: " + trainingYear));

        MonthSummary monthSummary = monthSummaryRepository.findByYearSummaryAndMonth(yearSummary, trainingMonth)
                .orElseThrow(() -> new EntityNotFoundException("No training data found for month: " + trainingMonth));

        if (monthSummary.getDuration().compareTo(record.trainingDuration()) < 0) {
            throw new IllegalArgumentException("Training duration to remove exceeds recorded duration.");
        }

        monthSummary.setDuration(monthSummary.getDuration().minus(record.trainingDuration()));

        if (monthSummary.getDuration().isZero()) {
            monthSummaryRepository.delete(monthSummary);
        } else {
            monthSummaryRepository.save(monthSummary);
        }

        if (monthSummaryRepository.countByYearSummary(yearSummary) == 0) {
            yearSummaryRepository.delete(yearSummary);
        }
    }

    @Transactional(readOnly = true)
    public void getTrainingSummaryByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with username: " + username));

        List<YearSummary> yearSummaries = yearSummaryRepository.findByTrainer(trainer);
        Map<Year, Map<Month, Duration>> trainingsDuration = yearSummaries.stream()
                .collect(Collectors.toMap(
                        YearSummary::getYear,
                        yearSummary -> yearSummary.getMonthDurationList().stream()
                                .collect(Collectors.toMap(
                                        MonthSummary::getMonth,
                                        MonthSummary::getDuration
                                ))
                ));
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), trainer.isActive(), trainingsDuration);
        messageSenderService.sendMessage(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN, dto);
    }
}
