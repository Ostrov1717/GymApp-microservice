package org.example.gym.domain.training.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.gym.common.utils.DurationEditor;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.training.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.time.Duration;
import java.util.List;

import static org.example.gym.common.ApiUrls.*;
import static org.example.gym.common.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private TrainingService trainingService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Duration.class, new DurationEditor());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void getTraineeTrainingsTest() {
        List<TrainingDTO.Response.TrainingProfileForTrainee> trainings = List.of(
                new TrainingDTO.Response.TrainingProfileForTrainee(TRAINING_NAME, PERIOD_TO, TRAINING_TYPE, DURATION, TRAINER_USERNAME),
                new TrainingDTO.Response.TrainingProfileForTrainee("Yoga morning class",
                        PERIOD_FROM, new TrainingType("YOGA"), DURATION, "Tomas.Kuk"));
        when(trainingService.findTraineeList(TRAINEE_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINER_USERNAME, String.valueOf(TRAINING_TYPE))).thenReturn(trainings);
        mockMvc.perform(get(TRAININGS_BASE + GET_TRAINEE_TRAININGS)
                        .param("traineeUsername", TRAINEE_USERNAME)
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("trainerUsername", TRAINER_USERNAME)
                        .param("trainingType", TRAINING_TYPE.getTrainingType())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingName").value("Evening jogging"))
                .andExpect(jsonPath("$[0].trainingType.trainingType").value("FITNESS"))
                .andExpect(jsonPath("$[0].trainerUsername").value("Monica.Dobs"))
                .andExpect(jsonPath("$[0].trainingDate").value("2024-12-31T09:45:00"))
                .andExpect(jsonPath("$[1].trainingName").value("Yoga morning class"))
                .andExpect(jsonPath("$[1].trainingType.trainingType").value("YOGA"))
                .andExpect(jsonPath("$[1].trainerUsername").value("Tomas.Kuk"))
                .andExpect(jsonPath("$[1].trainingDate").value("2024-01-01T09:15:00"));
        verify(trainingService, times(1))
                .findTraineeList(TRAINEE_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINER_USERNAME, String.valueOf(TRAINING_TYPE));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void getTrainerTrainingsTest() {
        List<TrainingDTO.Response.TrainingProfileForTrainer> trainings = List.of(
                new TrainingDTO.Response.TrainingProfileForTrainer("Fitness evening class", PERIOD_TO, TRAINING_TYPE, DURATION, TRAINEE_USERNAME),
                new TrainingDTO.Response.TrainingProfileForTrainer("Fitness morning class",
                        PERIOD_FROM, new TrainingType("FITNESS"), DURATION, "Tomas.Kuk"));
        when(trainingService.findTrainerList(TRAINER_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINEE_USERNAME)).thenReturn(trainings);
        mockMvc.perform(get(TRAININGS_BASE + GET_TRAINER_TRAININGS)
                        .param("trainerUsername", TRAINER_USERNAME)
                        .param("periodFrom", PERIOD_FROM.toString())
                        .param("periodTo", PERIOD_TO.toString())
                        .param("traineeUsername", TRAINEE_USERNAME)
                        .param("trainingType", TRAINING_TYPE.getTrainingType())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingName").value("Fitness evening class"))
                .andExpect(jsonPath("$[0].traineeUsername").value("Olga.Kurilenko"))
                .andExpect(jsonPath("$[0].trainingDate").value("2024-12-31T09:45:00"))
                .andExpect(jsonPath("$[1].trainingName").value("Fitness morning class"))
                .andExpect(jsonPath("$[1].traineeUsername").value("Tomas.Kuk"))
                .andExpect(jsonPath("$[1].trainingDate").value("2024-01-01T09:15:00"));
        verify(trainingService, times(1))
                .findTrainerList(TRAINER_USERNAME, PERIOD_FROM, PERIOD_TO, TRAINEE_USERNAME);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void createTrainingTest() {
        TrainingDTO.Request.NewTraining newTraining =
                new TrainingDTO.Request.NewTraining(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, DURATION);
        doNothing().when(trainingService).create(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, DURATION);
        mockMvc.perform(post(TRAININGS_BASE + CREATE_TRAINING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newTraining)))
                .andExpect(status().isOk());
        verify(trainingService, times(1))
                .create(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, DURATION);
    }
}