package org.example.gym.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.gym.domain.user.dto.JwtAuthenticationResponse;
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

import static org.example.gym.common.ApiUrls.*;
import static org.example.gym.common.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class UserAPIControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    @SneakyThrows
    @WithMockUser
    void login_Success() {
        mockMvc.perform(get(USER_BASE + USER_LOGIN))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    public void changeLoginTest_Success() {
        UserDTO.Request.ChangeLogin validRequest = new UserDTO.Request.ChangeLogin();
        validRequest.setNewPassword(NEW_PASSWORD);
        doNothing().when(userService).changePassword(USERNAME, NEW_PASSWORD);
        mockMvc.perform(put(USER_BASE + USER_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        verify(userService).changePassword(USERNAME, NEW_PASSWORD);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void changeLoginTest_InvalidNewPassword() {
        UserDTO.Request.ChangeLogin invalidRequest = new UserDTO.Request.ChangeLogin();
        invalidRequest.setNewPassword(INVALID_PASSWORD);
        mockMvc.perform(put(USER_BASE + USER_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.newPassword").value("New password is required"));
        verify(userService, never()).changePassword(anyString(), anyString());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    public void takeToken() {
        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse();
        expectedResponse.setToken("mocked-token");
        expectedResponse.setRefreshtoken("mocked-refresh-token");

        when(userService.signin(USERNAME)).thenReturn(expectedResponse);

        mockMvc.perform(post(USER_BASE + USER_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));
        verify(userService, times(1)).signin(USERNAME);
    }
}