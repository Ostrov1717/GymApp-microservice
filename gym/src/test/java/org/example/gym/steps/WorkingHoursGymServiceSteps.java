package org.example.gym.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.*;
import org.example.gym.config.ComponentTestConfiguration;
import org.example.gym.domain.messaging.ResponseStorageService;
import org.example.gym.domain.trainer.repository.TrainerRepository;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.user.repository.UserRepository;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.example.gym.common.ApiUrls.TRAINER_BASE;
import static org.example.gym.common.ApiUrls.WORKING_HOURS;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE_REQUEST;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class WorkingHoursGymServiceSteps extends ComponentTestConfiguration {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    TrainerRepository trainerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private ResponseStorageService responseStorageService;
    @Autowired
    private ObjectMapper objectMapper;
    private String trainerUsername;
    private String response;
    private User trainer;
    private TrainerWorkingHoursDTO workingHoursDTO;
    @Value("${auth.user.password}")
    private String userPassword;

    @Given("Trainer with username {string} works for the gym and her data exists in the system")
    public void coach_exists_in_the_system(String username) {
        initTestData();
        Optional<User> trainer = userRepository.findByUsername(username);
        assertTrue(trainer.isPresent(), "Trainer with username " + username + " should exist in system");
        this.trainer = trainer.get();
    }

    @And("her total training time is:")
    public void her_total_training_time_is(io.cucumber.datatable.DataTable table) {
        Map<Year, Map<Month, Duration>> trainingData = new HashMap<>();
        for (Map<String, String> row : table.asMaps()) {
            Year year = Year.of(Integer.parseInt(row.get("Year")));
            Month month = Month.valueOf(row.get("Month").toUpperCase());
            Duration duration = Duration.parse(row.get("Duration"));
            trainingData.computeIfAbsent(year, y -> new HashMap<>()).put(month, duration);
        }
        this.workingHoursDTO = new TrainerWorkingHoursDTO(trainerUsername, trainer.getFirstName(),
                trainer.getLastName(), trainer.isActive(), trainingData);
    }

    @When("Trainer {string} likes to know her summary of total training time and sends an HTTP request")
    public void coach_sends_request(String username) {
        this.trainerUsername = username;
    }

    @Then("the main application authenticates the coach and sends the request to microservice for working hours of this trainer")
    public void send_message_to_microservice() throws Exception {
        CompletableFuture<TrainerWorkingHoursDTO> future = new CompletableFuture<>();
        responseStorageService.storeResponseFuture(trainerUsername, future);
        future.complete(workingHoursDTO);
        MvcResult result = mockMvc.perform(get(TRAINER_BASE + WORKING_HOURS)
                        .with(user(trainerUsername).password(userPassword)))
                .andExpect(status().isOk())
                .andReturn();
        response = result.getResponse().getContentAsString();
    }

    @Then("the microservice receives request and sends total training time to main application")
    public void microservice_sends_response(DataTable table) throws Exception {
        jmsTemplate.convertAndSend(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN, objectMapper.writeValueAsString(workingHoursDTO));
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageConsumer consumer = session.createConsumer(session.createQueue(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE_REQUEST))) {
                Message message = consumer.receive(3000);
                assertNotNull(message, "Response message should be received");
                assertTrue(message instanceof TextMessage, "Response should be a TextMessage");
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("main app receives total training time from the microservice")
    public void receive_response_from_microservice() throws JsonProcessingException {
        TrainerWorkingHoursDTO responseDTO = objectMapper.readValue(response, TrainerWorkingHoursDTO.class);
        assertEquals(responseDTO.username(), workingHoursDTO.username());
        assertEquals(responseDTO.trainingsDuration().get(Year.of(2025)), workingHoursDTO.trainingsDuration().get(Year.of(2025)));
    }

    void initTestData() {
        jdbcTemplate.execute("INSERT INTO \"user\" (active, first_name, last_name, password, username) VALUES (true, 'Olga', 'Kurilenko', '$2a$10$dksj1.woqq4VzAH6gG61v.P7pwqMDNl91UKyYVfvIz/N7G2IGPhNy', 'Olga.Kurilenko'), (true, 'Monica', 'Dobs', '$2a$10$QclcSxyB.BIaFJvIeIDxmOG4CH9o6j48/YOZ/mrq3FX8e5/d6qMri', 'Monica.Dobs')");
        jdbcTemplate.execute("INSERT INTO trainee (address, date_of_birth, user_id) VALUES ('California', '1986-12-30', 1)");
        jdbcTemplate.execute("INSERT INTO training_type (id, training_type) VALUES (1, 'YOGA')");
        jdbcTemplate.execute("INSERT INTO trainer (specialization_id, user_id)  VALUES (1,2)");
    }
}
