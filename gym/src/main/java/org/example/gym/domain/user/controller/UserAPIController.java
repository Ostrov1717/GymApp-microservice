package org.example.gym.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.domain.user.dto.JwtAuthenticationResponse;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.example.gym.common.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(USER_BASE)
public class UserAPIController {
    private final UserService userService;

    //  3. Login (GET method)
    @GetMapping(USER_LOGIN)
    public void login() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET request to /login with username={}", userDetails.getUsername());
        log.info("Request successful. Username authenticated.");
    }

    //    4. Change Login (PUT method)
    @PutMapping(USER_CHANGE_LOGIN)
    public void changeLogin(@Valid @RequestBody UserDTO.Request.ChangeLogin dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT request to /change-login username={} with new password.", userDetails.getUsername());
        userService.changePassword(userDetails.getUsername(), dto.getNewPassword());
        log.info("Request successful. Password is changed.");
    }

    @PostMapping(USER_LOGIN)
    public JwtAuthenticationResponse takeToken() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Try to take token, username:{}", userDetails.getUsername());
        return userService.signin(userDetails.getUsername());
    }
}
