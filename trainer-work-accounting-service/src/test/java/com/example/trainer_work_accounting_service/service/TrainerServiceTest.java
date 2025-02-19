package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.messaging.MessageSenderService;
import com.example.trainer_work_accounting_service.repository.TrainerRepository;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.trainer_work_accounting_service.TestConstants.*;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private MessageSenderService messageSenderService;
    @InjectMocks
    private TrainerService trainerService;

    @Test
    void addTraining_TrainerExists_UpdateTrainingsDuration() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, DURATION, ActionType.ADD);
        Trainer.YearSummary yearSummary = new Trainer.YearSummary(trainingDTO.trainingDate().getYear(),
                List.of(new Trainer.MonthSummary(trainingDTO.trainingDate().getMonth().toString(),
                        trainingDTO.trainingDuration().getSeconds())));
        Trainer trainer = new Trainer(ID, USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, new ArrayList<>(List.of(yearSummary)));

        when(trainerRepository.findByUsername(trainingDTO.username())).thenReturn(Optional.of(trainer));

        trainerService.addTraining(trainingDTO);

        Trainer.MonthSummary monthSummary = trainer.getYearSummaries().get(0).getMonthDurationList().get(0);
        assertEquals(trainingDTO.trainingDuration().getSeconds() * 2, monthSummary.getDuration());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void addTraining_TrainerDoesNotExist_CreateNewTrainer() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, DURATION, ActionType.ADD);
        when(trainerRepository.findByUsername(trainingDTO.username())).thenReturn(Optional.empty());

        trainerService.addTraining(trainingDTO);

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);

        verify(trainerRepository).save(trainerCaptor.capture());
        Trainer savedTrainer = trainerCaptor.getValue();
        Trainer.YearSummary yearSummary = savedTrainer.getYearSummaries().get(0);
        Trainer.MonthSummary monthSummary = yearSummary.getMonthDurationList().get(0);
        assertEquals(trainingDTO.username(), savedTrainer.getUsername());
        assertEquals(trainingDTO.firstName(), savedTrainer.getFirstName());
        assertEquals(trainingDTO.lastName(), savedTrainer.getLastName());
        assertEquals(trainingDTO.active(), savedTrainer.isActive());
        assertEquals(trainingDTO.trainingDuration().getSeconds(), monthSummary.getDuration());
    }

    @Test
    void deleteTraining_TrainerExists_ReduceTrainingsDuration() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, Duration.parse("PT1H"), ActionType.DELETE);
        Trainer.YearSummary yearSummary = new Trainer.YearSummary(trainingDTO.trainingDate().getYear(),
                List.of(new Trainer.MonthSummary(trainingDTO.trainingDate().getMonth().toString(),
                        trainingDTO.trainingDuration().getSeconds())));
        Trainer trainer = new Trainer(ID, USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, new ArrayList<>(List.of(yearSummary)));

        when(trainerRepository.findByUsername(trainingDTO.username())).thenReturn(Optional.of(trainer));

        trainerService.deleteTraining(trainingDTO);

        Trainer.MonthSummary monthSummary = trainer.getYearSummaries().get(0).getMonthDurationList().get(0);
        assertEquals(0, monthSummary.getDuration());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deleteTraining_TrainerDoesNotExist_Exception() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, DURATION, ActionType.DELETE);

        when(trainerRepository.findByUsername(trainingDTO.username())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> trainerService.deleteTraining(trainingDTO));

        assertEquals("Trainer not found for username: " + trainingDTO.username(), exception.getMessage());
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void getTrainingSummaryByUsername_Success() {
        Trainer trainer = new Trainer(ID, USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, new ArrayList<>());
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));

        trainerService.getTrainingSummaryByUsername(USERNAME);

        ArgumentCaptor<TrainerWorkingHoursDTO> dtoCaptor = ArgumentCaptor.forClass(TrainerWorkingHoursDTO.class);
        verify(messageSenderService).sendMessage(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), dtoCaptor.capture());

        TrainerWorkingHoursDTO sentDto = dtoCaptor.getValue();
        assertEquals(trainer.getUsername(), sentDto.username());
        assertEquals(trainer.getFirstName(), sentDto.firstName());
        assertEquals(trainer.getLastName(), sentDto.lastName());
        assertEquals(trainer.isActive(), sentDto.active());
    }

    @Test
    void getTrainingSummaryByUsername_TrainerNotFound() {
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainingSummaryByUsername(USERNAME));

        assertEquals("Trainer not found for username: " + USERNAME, exception.getMessage());
        verify(messageSenderService, never()).sendMessage(any(), any());
    }
}