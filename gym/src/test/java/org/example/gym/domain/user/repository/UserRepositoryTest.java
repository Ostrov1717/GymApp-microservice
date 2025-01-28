package org.example.gym.domain.user.repository;

import org.example.gym.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsernameTest_Success() {
        String username="Olga.Kurilenko";
        Optional<User> user=userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        assertEquals(user.get().getFirstName(),"Olga");
        assertEquals(user.get().getLastName(),"Kurilenko");
        assertEquals(user.get().getUsername(),"Olga.Kurilenko");
        assertEquals(user.get().isActive(),true);
    }

    @Test
    void findByUsernameTest_Failure() {
        assertFalse(userRepository.findByUsername("Doctor.Watson").isPresent());
    }
}