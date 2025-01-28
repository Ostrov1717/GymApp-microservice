package org.example.gym.common;

import org.example.gym.common.exception.ProjectExceptionHandler;
import org.example.gym.config.RequestLoggingFilter;
import org.example.gym.domain.trainee.controller.TraineeController;
import org.example.gym.domain.trainer.controller.TrainerController;
import org.example.gym.domain.training.controller.TrainingController;
import org.example.gym.domain.user.controller.UserAPIController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private UserAPIController userAPIController;

    @Autowired
    private TraineeController traineeController;

    @Autowired
    private TrainerController trainerController;

    @Autowired
    private TrainingController trainingController;

    @Autowired
    RequestLoggingFilter filter;

    @Autowired
    ProjectExceptionHandler handler;

    @Test
    void contextLoads() {
        assertThat(userAPIController).isNotNull();
        assertThat(traineeController).isNotNull();
        assertThat(trainerController).isNotNull();
        assertThat(trainingController).isNotNull();
        assertThat(filter).isNotNull();
        assertThat(handler).isNotNull();
    }

}
