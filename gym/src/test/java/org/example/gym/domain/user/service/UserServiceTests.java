package org.example.gym.domain.user.service;

import org.example.gym.common.exception.AuthenticationException;
import org.example.gym.domain.user.metrics.UserMetrics;
import org.example.gym.domain.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = org.example.gym.App.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMetrics userMetrics;
    private String username="Olga.Kurilenko";
    private String password="WRqqRQMsoy";
}
