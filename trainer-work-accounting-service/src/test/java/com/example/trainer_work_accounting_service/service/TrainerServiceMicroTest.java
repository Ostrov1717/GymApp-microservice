package com.example.trainer_work_accounting_service.service;

import com.example.trainer_work_accounting_service.TestConstants;
import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.messaging.MessageSenderServiceMicro;
import com.example.trainer_work_accounting_service.repository.TrainerRepositoryMicro;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.junit.jupiter.api.Assertions;
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

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceMicroTest {
    @Mock
    private TrainerRepositoryMicro trainerRepositoryMicro;
    @Mock
    private MessageSenderServiceMicro messageSenderServiceMicro;
    @InjectMocks
    private TrainerServiceMicro trainerServiceMicro;

    @Test
    void addTraining_TrainerExists_UpdateTrainingsDuration() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.ADD);
        Trainer.YearSummary yearSummary = new Trainer.YearSummary(trainingDTO.trainingDate().getYear(),
                List.of(new Trainer.MonthSummary(trainingDTO.trainingDate().getMonth().toString(),
                        trainingDTO.trainingDuration().getSeconds())));
        Trainer trainer = new Trainer(TestConstants.ID, TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, new ArrayList<>(List.of(yearSummary)));

        when(trainerRepositoryMicro.findByUsername(trainingDTO.username())).thenReturn(Optional.of(trainer));

        trainerServiceMicro.addTraining(trainingDTO);

        Trainer.MonthSummary monthSummary = trainer.getYearSummaries().get(0).getMonthDurationList().get(0);
        assertEquals(trainingDTO.trainingDuration().getSeconds() * 2, monthSummary.getDuration());
        verify(trainerRepositoryMicro).save(trainer);
    }

    @Test
    void addTraining_TrainerDoesNotExist_CreateNewTrainer() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.ADD);
        when(trainerRepositoryMicro.findByUsername(trainingDTO.username())).thenReturn(Optional.empty());

        trainerServiceMicro.addTraining(trainingDTO);

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);

        verify(trainerRepositoryMicro).save(trainerCaptor.capture());
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
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, Duration.parse("PT1H"), ActionType.DELETE);
        Trainer.YearSummary yearSummary = new Trainer.YearSummary(trainingDTO.trainingDate().getYear(),
                List.of(new Trainer.MonthSummary(trainingDTO.trainingDate().getMonth().toString(),
                        trainingDTO.trainingDuration().getSeconds())));
        Trainer trainer = new Trainer(TestConstants.ID, TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, new ArrayList<>(List.of(yearSummary)));

        when(trainerRepositoryMicro.findByUsername(trainingDTO.username())).thenReturn(Optional.of(trainer));

        trainerServiceMicro.deleteTraining(trainingDTO);

        Trainer.MonthSummary monthSummary = trainer.getYearSummaries().get(0).getMonthDurationList().get(0);
        assertEquals(0, monthSummary.getDuration());
        verify(trainerRepositoryMicro).save(trainer);
    }

    @Test
    void deleteTraining_TrainerDoesNotExist_Exception() {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.DELETE);

        when(trainerRepositoryMicro.findByUsername(trainingDTO.username())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> trainerServiceMicro.deleteTraining(trainingDTO));

        assertEquals("Trainer not found for username: " + trainingDTO.username(), exception.getMessage());
        verify(trainerRepositoryMicro, never()).save(any());
    }

    @Test
    void getTrainingSummaryByUsername_Success() {
        Trainer trainer = new Trainer(TestConstants.ID, TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, new ArrayList<>());
        when(trainerRepositoryMicro.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(trainer));

        trainerServiceMicro.getTrainingSummaryByUsername(TestConstants.USERNAME);

        ArgumentCaptor<TrainerWorkingHoursDTO> dtoCaptor = ArgumentCaptor.forClass(TrainerWorkingHoursDTO.class);
        verify(messageSenderServiceMicro).sendMessage(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), dtoCaptor.capture());

        TrainerWorkingHoursDTO sentDto = dtoCaptor.getValue();
        assertEquals(trainer.getUsername(), sentDto.username());
        assertEquals(trainer.getFirstName(), sentDto.firstName());
        assertEquals(trainer.getLastName(), sentDto.lastName());
        assertEquals(trainer.isActive(), sentDto.active());
    }

    @Test
    void getTrainingSummaryByUsername_TrainerNotFound() {
        when(trainerRepositoryMicro.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> trainerServiceMicro.getTrainingSummaryByUsername(TestConstants.USERNAME));

        Assertions.assertEquals("Trainer not found for username: " + TestConstants.USERNAME, exception.getMessage());
        verify(messageSenderServiceMicro, never()).sendMessage(any(), any());
    }
}