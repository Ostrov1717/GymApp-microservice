package org.example.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gym.config.ComponentTest;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.entity.Training;
import org.example.gym.domain.training.repository.TrainingRepository;
import org.example.gym.domain.training.service.TrainingService;
import org.example.shareddto.TrainerTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.gym.common.ApiUrls.CREATE_TRAINING;
import static org.example.gym.common.ApiUrls.TRAININGS_BASE;
import static org.example.gym.common.TestConstants.*;
import static org.example.gym.common.TestConstants.DURATION;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ComponentTest
@AutoConfigureMockMvc
public class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    @Transactional
    void initTestData() {

        jdbcTemplate.execute("INSERT INTO \"user\" (active, first_name, last_name, password, username) VALUES (true, 'Olga', 'Kurilenko', '$2a$10$dksj1.woqq4VzAH6gG61v.P7pwqMDNl91UKyYVfvIz/N7G2IGPhNy', 'Olga.Kurilenko'), (true, 'Monica', 'Dobs', '$2a$10$QclcSxyB.BIaFJvIeIDxmOG4CH9o6j48/YOZ/mrq3FX8e5/d6qMri', 'Monica.Dobs')");
        jdbcTemplate.execute("INSERT INTO trainee (address, date_of_birth, user_id) VALUES ('California', '1986-12-30', 1)");
        jdbcTemplate.execute("INSERT INTO training_type (id, training_type) VALUES (1, 'YOGA')");
        jdbcTemplate.execute("INSERT INTO trainer (specialization_id, user_id)  VALUES (1,2)");
    }
    @Test
    @WithMockUser
    void testCreateTraining() throws Exception {

        TrainingDTO.Request.NewTraining newTraining =
                new TrainingDTO.Request.NewTraining(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, DURATION);

        mockMvc.perform(post(TRAININGS_BASE + CREATE_TRAINING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newTraining)))
                .andExpect(status().isOk());

        List<Training> training=trainingRepository.findTrainingsByTrainerAndCriteria(TRAINER_USERNAME,PERIOD_TO,null,null);
        assertThat(training).isNotNull();
        assertThat(training.size()).isEqualTo(1);

        String jsonMessage = (String) jmsTemplate.receiveAndConvert(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE);
        TrainerTrainingDTO dto = mapper.readValue(jsonMessage, TrainerTrainingDTO.class);
        assertThat(dto).isNotNull();
        assertThat(dto.username()).contains(TRAINER_USERNAME);
        assertThat(dto.trainingDuration()).isEqualTo(DURATION);
        assertThat(dto.trainingDate()).isEqualTo(PERIOD_TO);
    }
}
