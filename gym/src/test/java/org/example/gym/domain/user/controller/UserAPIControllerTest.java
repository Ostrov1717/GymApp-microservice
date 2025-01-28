package org.example.gym.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.gym.common.exception.AuthenticationException;
import org.example.gym.config.MetricsFilter;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.gym.common.ApiUrls.*;
import static org.example.gym.common.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserAPIController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MetricsFilter.class))
public class UserAPIControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    public void loginTest_Success() {
        mockMvc.perform(get(USER_BASE + USER_LOGIN)
                        .param("username", USERNAME)
                        .param("password", PASSWORD)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
        verify(userService, times(1)).authenticate(USERNAME, PASSWORD);
    }

    @Test
    @SneakyThrows
    public void loginTest_Failure() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(userService).authenticate(USERNAME, PASSWORD + "1");
        mockMvc.perform(get(USER_BASE + USER_LOGIN)
                        .param("username", USERNAME)
                        .param("password", PASSWORD + "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
        verify(userService, times(1)).authenticate(USERNAME, PASSWORD + "1");
    }

    @Test
    @SneakyThrows
    public void loginTest_InvalidUsername() {
        mockMvc.perform(get(USER_BASE + USER_LOGIN)
                        .param("username", INVALID_USERNAME)
                        .param("password", PASSWORD)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username is required"));
        verifyNoInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void loginTest_InvalidPassword() {
        mockMvc.perform(get(USER_BASE + USER_LOGIN)
                        .param("username", USERNAME)
                        .param("password", INVALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password is required"));
        verifyNoInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void changeLoginTest_Success() {
        UserDTO.Request.ChangeLogin validRequest = new UserDTO.Request.ChangeLogin();
        validRequest.setUsername(USERNAME);
        validRequest.setPassword(PASSWORD);
        validRequest.setNewPassword(NEW_PASSWORD);
        mockMvc.perform(put(USER_BASE + USER_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        verify(userService).changePassword(USERNAME, PASSWORD, NEW_PASSWORD);
    }

    @Test
    @SneakyThrows
    public void changeLoginTest_InvalidNewPassword() {
        UserDTO.Request.ChangeLogin invalidRequest = new UserDTO.Request.ChangeLogin();
        invalidRequest.setUsername(USERNAME);
        invalidRequest.setPassword(PASSWORD);
        invalidRequest.setNewPassword(INVALID_PASSWORD);
        mockMvc.perform(put(USER_BASE + USER_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.newPassword").value("New password is required"));
        verify(userService, never()).changePassword(anyString(), anyString(), anyString());
    }
}