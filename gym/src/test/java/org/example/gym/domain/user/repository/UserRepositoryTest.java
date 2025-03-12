package org.example.gym.domain.user.repository;

import org.example.gym.config.TestCleanup;
import org.example.gym.domain.user.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestCleanup cleanup;

    @BeforeAll
    @Transactional
    void initTestData() {
        jdbcTemplate.execute("INSERT INTO \"user\" (active, first_name, last_name, password, username) VALUES (true, 'Olga', 'Kurilenko', '$2a$10$dksj1.woqq4VzAH6gG61v.P7pwqMDNl91UKyYVfvIz/N7G2IGPhNy', 'Olga.Kurilenko')");
    }

    @Test
    void findByUsernameTest_Success() {
        String username="Olga.Kurilenko";
        Optional<User> user=userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        assertEquals(user.get().getFirstName(),"Olga");
        assertEquals(user.get().getLastName(),"Kurilenko");
        assertEquals(user.get().getUsername(),"Olga.Kurilenko");
        assertTrue(user.get().isActive());
    }

    @Test
    void findByUsernameTest_Failure() {
        assertFalse(userRepository.findByUsername("Doctor.Watson").isPresent());
    }
}