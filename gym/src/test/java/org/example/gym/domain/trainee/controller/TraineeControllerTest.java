package org.example.gym.domain.trainee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.gym.common.exception.AuthenticationException;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.trainee.service.TraineeService;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.trainer.service.TrainerService;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.example.gym.common.ApiUrls.*;
import static org.example.gym.common.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class TraineeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void traineeRegistrationTest() {
        TraineeDTO.Request.TraineeRegistration validRequest = new TraineeDTO.Request.TraineeRegistration(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS);
        UserDTO.Response.Login expectedResponse =
                new UserDTO.Response.Login(USERNAME, PASSWORD);
        when(traineeService.create(FIRST_NAME, LAST_NAME, ADDRESS, DATE_OF_BIRTH)).thenReturn(expectedResponse);
        mockMvc.perform(post(TRAINEE_BASE + REGISTER_TRAINEE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.password").isString());
        verify(traineeService, times(1)).create(FIRST_NAME, LAST_NAME, ADDRESS, DATE_OF_BIRTH);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void getTraineeProfileTest() {
        TraineeDTO.Response.TraineeProfile profile = new TraineeDTO.Response.TraineeProfile(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS, true, new HashSet<>());
        when(traineeService.findByUsername(USERNAME)).thenReturn(profile);
        mockMvc.perform(get(TRAINEE_BASE + GET_TRAINEE_PROFILE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"))
                .andExpect(jsonPath("$.address").value(ADDRESS))
                .andExpect(jsonPath("$.active").value("true"))
                .andExpect(jsonPath("$.trainers").isArray());
        verify(traineeService, times(1)).findByUsername(USERNAME);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void updateTraineeProfileTest() {
        TraineeDTO.Request.TraineeUpdate traineeUpdate =
                new TraineeDTO.Request.TraineeUpdate(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS, true);
        TraineeDTO.Response.TraineeProfileFull traineeNewProfile =
                new TraineeDTO.Response.TraineeProfileFull(USERNAME, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS, true, new HashSet<>());
        when(traineeService.update(FIRST_NAME, LAST_NAME, USERNAME, ADDRESS, DATE_OF_BIRTH, true)).thenReturn(traineeNewProfile);
        mockMvc.perform(put(TRAINEE_BASE + UPDATE_TRAINEE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(traineeUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"))
                .andExpect(jsonPath("$.address").value(ADDRESS))
                .andExpect(jsonPath("$.active").value("true"))
                .andExpect(jsonPath("$.trainers").isArray());
        verify(traineeService, times(1))
                .update(FIRST_NAME, LAST_NAME, USERNAME, ADDRESS, DATE_OF_BIRTH, true);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void deleteTraineeProfileTest_Success() {
        UserDTO.Request.UserLogin trainee = new UserDTO.Request.UserLogin(USERNAME, PASSWORD);
        doNothing().when(traineeService).delete(anyString());
        mockMvc.perform(delete(TRAINEE_BASE + DELETE_TRAINEE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainee)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(traineeService, times(1)).delete(USERNAME);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void deleteTraineeProfileTest_AuthenticationError() {
        UserDTO.Request.UserLogin trainee = new UserDTO.Request.UserLogin(USERNAME, PASSWORD);
        doThrow(new AuthenticationException("Invalid username or password")).when(traineeService).delete(anyString());
        mockMvc.perform(delete(TRAINEE_BASE + DELETE_TRAINEE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainee)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
        verify(traineeService, times(1)).delete(anyString());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void updateTraineeTrainersTest() {
        Set<TrainerDTO.Response.TrainerUsername> newTrainersRequest = new HashSet<>();
        newTrainersRequest.add(new TrainerDTO.Response.TrainerUsername("Monica.Dobs"));
        newTrainersRequest.add(new TrainerDTO.Response.TrainerUsername("Wallace.Tim"));

        TraineeDTO.Request.UpdateTrainers newTrainersDTO = new TraineeDTO.Request.UpdateTrainers(newTrainersRequest);
        Set<TrainerDTO.Response.TrainerSummury> newTrainersResponse = new HashSet<>();
        newTrainersResponse.add(new TrainerDTO.Response.TrainerSummury("Monica.Dobs", "Monica", "Dobs", null));
        newTrainersResponse.add(new TrainerDTO.Response.TrainerSummury("Wallace.Tim", "Wallace", "Tim", null));

        Set<Trainer> trainers = new HashSet<>();
        when(trainerService.getTrainerFromList(newTrainersRequest)).thenReturn(trainers);
        when(traineeService.updateTraineeTrainers(USERNAME, trainers)).thenReturn(newTrainersResponse);

        mockMvc.perform(put(TRAINEE_BASE + UPDATE_TRAINERS_LIST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"Brad.Pitt\", \"password\": \"12345\", \"trainersUsernames\": [\"Monica.Dobs\", \"Wallace.Tim\"]}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Monica.Dobs"))
                .andExpect(jsonPath("$[1].username").value("Wallace.Tim"));
        verify(trainerService, times(1)).getTrainerFromList(newTrainersRequest);
        verify(traineeService, times(1)).updateTraineeTrainers(USERNAME, new HashSet<>());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void activateTraineeTest() {
        UserDTO.Request.ActivateOrDeactivate trainee = new UserDTO.Request.ActivateOrDeactivate(true);
        when(userService.activate(USERNAME)).thenReturn(true);
        mockMvc.perform(patch(TRAINEE_BASE + UPDATE_TRAINEE_STATUS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainee)))
                .andExpect(status().isOk());
        verify(userService, times(1)).activate(USERNAME);
        verify(userService, never()).deactivate(anyString());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void deactivateTraineeTest() {
        UserDTO.Request.ActivateOrDeactivate trainee = new UserDTO.Request.ActivateOrDeactivate(false);
        when(userService.deactivate(USERNAME)).thenReturn(false);
        mockMvc.perform(patch(TRAINEE_BASE + UPDATE_TRAINEE_STATUS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainee)))
                .andExpect(status().isOk());
        verify(userService, times(1)).deactivate(USERNAME);
        verify(userService, never()).activate(anyString());
    }
}