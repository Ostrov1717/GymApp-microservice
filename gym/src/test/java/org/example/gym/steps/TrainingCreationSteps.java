package org.example.gym.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.gym.config.ComponentTestConfiguration;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.entity.Training;
import org.example.gym.domain.training.repository.TrainingRepository;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.gym.common.ApiUrls.CREATE_TRAINING;
import static org.example.gym.common.ApiUrls.TRAININGS_BASE;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TrainingCreationSteps extends ComponentTestConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private ObjectMapper mapper;
    private TrainingDTO.Request.NewTraining newTraining;
    private ResultActions response;
    @Value("${auth.user.password}")
    private String userPassword;

    @Given("trainer {string} schedules a training session on {string} lasting {string}")
    public void trainerSchedulesTrainingSession(String trainer, String date, String duration) {
        initTestData();
        newTraining = new TrainingDTO.Request.NewTraining();
        newTraining.setTrainerUsername(trainer);
        newTraining.setTrainingDate(LocalDateTime.parse(date));
        newTraining.setTrainingDuration(Duration.parse(duration));
    }

    @And("the workout is called {string}")
    public void workoutIsCalled(String trainingName) {
        newTraining.setTrainingName(trainingName);
    }

    @And("the trainee {string} is assigned to the session")
    public void traineeIsAssignedToSession(String trainee) {
        newTraining.setTraineeUsername(trainee);
    }

    @When("the service processes the training session request")
    public void serviceValidatesWorkoutData() throws Exception {
        response = mockMvc.perform(post(TRAININGS_BASE + CREATE_TRAINING)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(newTraining.getTrainerUsername()).password(userPassword))
                .content(mapper.writeValueAsString(newTraining)));
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int status) throws Exception {
        response.andExpect(status().is(status));
    }

    @And("the service sends a message to ActiveMQ with training session details")
    public void serviceSendsMessageToActiveMQ() throws JsonProcessingException {
        String jsonMessage = (String) jmsTemplate.receiveAndConvert(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE);
        TrainerTrainingDTO dto = mapper.readValue(jsonMessage, TrainerTrainingDTO.class);
        assertThat(dto).isNotNull();
        assertThat(dto.username()).contains(newTraining.getTrainerUsername());
        assertThat(dto.trainingDuration()).isEqualTo(newTraining.getTrainingDuration());
        assertThat(dto.trainingDate()).isEqualTo(newTraining.getTrainingDate());
    }

    @And("the service stores the training session in the database")
    public void serviceStoresTrainingSessionInDatabase() {
        List<Training> training = trainingRepository.findTrainingsByTrainerAndCriteria(newTraining.getTrainerUsername(),
                newTraining.getTrainingDate(), newTraining.getTrainingDate(), newTraining.getTraineeUsername());
        assertThat(training.size()).isEqualTo(1);
        assertThat(training.get(0).getTrainer().getUser().getUsername()).isEqualTo(newTraining.getTrainerUsername());
    }

    void initTestData() {
        jdbcTemplate.execute("INSERT INTO \"user\" (active, first_name, last_name, password, username) VALUES (true, 'Olga', 'Kurilenko', '$2a$10$dksj1.woqq4VzAH6gG61v.P7pwqMDNl91UKyYVfvIz/N7G2IGPhNy', 'Olga.Kurilenko'), (true, 'Monica', 'Dobs', '$2a$10$QclcSxyB.BIaFJvIeIDxmOG4CH9o6j48/YOZ/mrq3FX8e5/d6qMri', 'Monica.Dobs')");
        jdbcTemplate.execute("INSERT INTO trainee (address, date_of_birth, user_id) VALUES ('California', '1986-12-30', 1)");
        jdbcTemplate.execute("INSERT INTO training_type (id, training_type) VALUES (1, 'YOGA')");
        jdbcTemplate.execute("INSERT INTO trainer (specialization_id, user_id)  VALUES (1,2)");
    }
}


