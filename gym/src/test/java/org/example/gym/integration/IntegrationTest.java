package org.example.gym.integration;

import org.example.gym.config.IntegrationTestConfiguration;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.gym.common.ApiUrls.*;
import static org.example.gym.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;


public class IntegrationTest extends IntegrationTestConfiguration {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final TrainingDTO.Request.NewTraining newTraining =
            new TrainingDTO.Request.NewTraining(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, DURATION);

    @Value("${auth.user.password}")
    private String userPassword;

    @Test
    public void testCreateTraining() throws Exception {
        jdbcTemplate.execute("TRUNCATE TABLE training RESTART IDENTITY CASCADE");
        mongoTemplate.getDb().drop();
        RestTemplate restTemplate = new RestTemplate();
        String credentials = TRAINER_USERNAME + ":" + userPassword;
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String url = "http://localhost:" + port + TRAININGS_BASE + CREATE_TRAINING;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);

        HttpEntity<TrainingDTO.Request.NewTraining> request = new HttpEntity<>(newTraining, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM training", Integer.class);
        assertEquals(1, count);

        Thread.sleep(3000);

        long mongoCount = mongoTemplate.getCollection("trainers").countDocuments();
        assertEquals(1, mongoCount);
    }

    @Test
    void testCreateTraining_InvalidTrainingDuration() {
        String url = "http://localhost:" + port + TRAININGS_BASE + CREATE_TRAINING;
        String credentials = TRAINER_USERNAME + ":" + userPassword;
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + base64Creds);

        TrainingDTO.Request.NewTraining invalidTraining =
                new TrainingDTO.Request.NewTraining(TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, PERIOD_TO, Duration.ofMinutes(5));

        HttpEntity<TrainingDTO.Request.NewTraining> request = new HttpEntity<>(invalidTraining, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void totalWorkingTime() {
        RestTemplate restTemplate = new RestTemplate();
        String credentials = TRAINER_USERNAME + ":" + userPassword;
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String url = "http://localhost:" + port + TRAINER_BASE + WORKING_HOURS;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<TrainerWorkingHoursDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, TrainerWorkingHoursDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(TRAINER_USERNAME, response.getBody().username());

        Optional<Month> monthOptional = response.getBody().trainingsDuration().get(Year.of(PERIOD_TO.getYear())).keySet().stream().findFirst();
        assertTrue(monthOptional.isPresent());
        assertEquals(PERIOD_TO.getMonth().name(), monthOptional.get().toString());
        assertEquals(DURATION.getSeconds(),
                response.getBody().trainingsDuration().get(Year.of(PERIOD_TO.getYear())).get(PERIOD_TO.getMonth()).getSeconds());
    }

    @Test
    void testGetTrainerWorkingHours_NotAuthenticate() {
        String credentials = "WRONG" + ":" + "PASSWORD";
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String url = "http://localhost:" + port + TRAINER_BASE + WORKING_HOURS;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<TrainerWorkingHoursDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, TrainerWorkingHoursDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


}
