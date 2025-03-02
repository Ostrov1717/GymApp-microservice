package org.example.gym.domain.user.repository;

import org.example.gym.App;
import org.example.gym.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = App.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryBootTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserRepository() {
        Optional<User> user = userRepository.findByUsername("Olga.Kurilenko");
        assertTrue(user.isPresent());
        assertEquals("Olga.Kurilenko", user.get().getUsername());
        assertFalse(userRepository.findByUsername("Doctor.Watson").isPresent());
    }
}
