package com.example.trainer_work_accounting_service.steps;

import com.example.trainer_work_accounting_service.TestConstants;
import com.example.trainer_work_accounting_service.repository.TrainerRepositoryMicro;
import com.example.trainer_work_accounting_service.config.CucumberComponentTestConfiguration;
import com.example.trainer_work_accounting_service.domain.Trainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.time.Duration;
import java.util.Optional;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MicroserviceTrainingCreationSteps extends CucumberComponentTestConfiguration {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private TrainerRepositoryMicro trainerRepositoryMicro;
    @Autowired
    private ObjectMapper objectMapper;

    @Given("the training event with action {string}")
    public void givenCreateTrainingEvent(String action) throws JsonProcessingException {
        TrainerTrainingDTO trainingDTO = new TrainerTrainingDTO(TestConstants.TRAINER_USERNAME_AUTHEN, TestConstants.TRAINER_FIRST_NAME_AUTHEN,
                TestConstants.TRAINER_LAST_NAME_AUTHEN, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.valueOf(action));
        String jsonMessage = objectMapper.writeValueAsString(trainingDTO);
        jmsTemplate.convertAndSend(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE, jsonMessage);
    }

    @When("the message is consumed from the queue")
    public void whenMessageIsConsumed() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Then("the training should be saved in the database")
    public void thenTrainingShouldBeSaved() {
        Optional<Trainer> trainer = trainerRepositoryMicro.findByUsername(TestConstants.TRAINER_USERNAME_AUTHEN);
        assertTrue(trainer.isPresent(), "Training saved in the database");
        assertEquals(trainer.get().getYearSummaries().get(0).getYear(), TestConstants.TRAINING_DATE.getYear());
        assertEquals(trainer.get().getYearSummaries().get(0).getMonthDurationList().get(0).getMonth(), TestConstants.TRAINING_DATE.getMonth().toString());
        assertEquals(trainer.get().getYearSummaries().get(0).getMonthDurationList().get(0).getDuration(), TestConstants.DURATION.getSeconds());
    }

    @Then("the training should be removed from the database")
    public void thenTrainingShouldBeRemoved() {
        Optional<Trainer> trainer = trainerRepositoryMicro.findByUsername(TestConstants.TRAINER_USERNAME_AUTHEN);
        assertTrue(trainer.isPresent(), "Training removed from the database");
        assertEquals(trainer.get().getYearSummaries().get(0).getMonthDurationList().get(0).getDuration(), Duration.ZERO.getSeconds());
    }
}
