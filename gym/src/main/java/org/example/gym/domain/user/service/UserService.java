package org.example.gym.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.AuthenticationException;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.user.dto.JwtAuthenticationResponse;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.user.repository.UserRepository;
import org.example.gym.domain.user.security.JWTService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public JwtAuthenticationResponse signin(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshtoken(refreshToken);
        return jwtAuthenticationResponse;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        log.info("Changing password of User with username: {}", username);
        User user = findUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("Password successfully changed");
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found."));
    }

    @Transactional
    private boolean updateActiveStatus(String username, boolean isActive) {
        User user = findUserByUsername(username);
        user.setActive(isActive);
        return true;
    }

    @Transactional
    public boolean deactivate(String username) {
        log.info("Deactivating User with username: {}", username);
        boolean deactivated = updateActiveStatus(username, false);
        log.info("User with username: {} deactivated", username);
        return deactivated;
    }

    @Transactional
    public boolean activate(String username) {
        log.info("Activating User with username: {}", username);
        boolean activated = updateActiveStatus(username, true);
        log.info("User with username: {} activated", username);
        return activated;
    }

    public long getActiveUserCount() {
        log.info("Counting active users");
        return userRepository.countByActive(true);
    }

    public String generatePassword() {
        Random random = new Random();
        return random.ints(33, 122)
                .filter(Character::isLetter)
                .limit(10)
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    @Transactional
    public String generateUserName(String firstName, String lastName) {
        String baseUserName = firstName + "." + lastName;
        long count = userRepository.findAll().stream()
                .filter(tr -> tr.getFirstName().equals(firstName) && tr.getLastName().equals(lastName))
                .count();
        return count > 0 ? baseUserName + count : baseUserName;
    }
}
