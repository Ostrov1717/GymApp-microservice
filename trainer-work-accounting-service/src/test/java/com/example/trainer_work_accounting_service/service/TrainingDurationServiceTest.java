package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.domain.MonthSummary;
import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.domain.YearSummary;
import com.example.trainer_work_accounting_service.messaging.MessageSenderService;
import com.example.trainer_work_accounting_service.repository.TrainerRepository;
import com.example.trainer_work_accounting_service.repository.YearSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static com.example.trainer_work_accounting_service.TestConstants.*;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingDurationServiceTest {
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private YearSummaryRepository yearSummaryRepository;
    @Mock
    private MessageSenderService messageSenderService;
    @InjectMocks
    private TrainingDurationService trainingDurationService;

    @Test
    void testGetTrainingSummaryByUsername_Success() {
        Trainer trainer = new Trainer(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE);
        YearSummary yearSummary = new YearSummary();
        yearSummary.setYear(Year.of(2025));
        yearSummary.setMonthDurationList(new ArrayList<>());

        MonthSummary monthSummary = new MonthSummary();
        monthSummary.setMonth(Month.JANUARY);
        monthSummary.setDuration(Duration.ofHours(10));
        monthSummary.setYearSummary(yearSummary);

        yearSummary.getMonthDurationList().add(monthSummary);

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(yearSummaryRepository.findByTrainer(trainer)).thenReturn(Collections.singletonList(yearSummary));

        trainingDurationService.getTrainingSummaryByUsername(USERNAME);

        verify(messageSenderService, Mockito.times(1)).sendMessage(
                eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), any(TrainerWorkingHoursDTO.class)
        );
    }

    @Test
    void testGetTrainingSummaryByUsername_TrainerNotFound() {
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            trainingDurationService.getTrainingSummaryByUsername(USERNAME);
        });

        assertEquals("Trainer not found with username: " + USERNAME, thrown.getMessage());
    }
}
