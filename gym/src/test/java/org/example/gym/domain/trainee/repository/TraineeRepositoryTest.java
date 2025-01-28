package org.example.gym.domain.trainee.repository;

import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.example.gym.common.TestConstants.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TraineeRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TraineeRepository traineeRepository;

    @Test
    void createTrainee() {
        Trainee newTrainee = new Trainee(new User(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, false), ADDRESS, DATE_OF_BIRTH);

        testEntityManager.persist(newTrainee);

        Optional<Trainee> trainee=traineeRepository.findByUserUsername(USERNAME);

        assertTrue(trainee.isPresent());
        assertEquals(trainee.get().getUser().getFirstName(),FIRST_NAME);
        assertEquals(trainee.get().getUser().getLastName(),LAST_NAME);
        assertEquals(trainee.get().getUser().getPassword(),PASSWORD);
        assertEquals(trainee.get().getUser().isActive(),false);
        assertEquals(trainee.get().getAddress(),ADDRESS);
        assertEquals(trainee.get().getDateOfBirth(),DATE_OF_BIRTH);
    }
}