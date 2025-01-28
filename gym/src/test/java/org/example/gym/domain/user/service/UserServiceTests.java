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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @Order(1)
    public void authenticateTest(){
        String wrongPassword="111";

        assertTrue(userService.authenticate(username,password));
        Exception ex=assertThrows(AuthenticationException.class,()->userService.authenticate(username,wrongPassword));
        assertEquals("Invalid username or password",ex.getMessage());
    }

    @Test
    @Order(2)
    public void changePasswordTest(){
        assertTrue(userService.authenticate(username,password));
        userService.changePassword(username,password,"12345");
        assertTrue(userService.authenticate(username,"12345"));
        Exception ex=assertThrows(AuthenticationException.class,()->userService.authenticate(username,password));
        assertEquals("Invalid username or password",ex.getMessage());

    }
}
