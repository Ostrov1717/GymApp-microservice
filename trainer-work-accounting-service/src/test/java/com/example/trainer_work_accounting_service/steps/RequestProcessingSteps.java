package com.example.trainer_work_accounting_service.steps;

import com.example.trainer_work_accounting_service.TestConstants;
import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.repository.TrainerRepositoryMicro;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.shareddto.SharedConstants.TRANSACTION_ID;
import static org.junit.jupiter.api.Assertions.*;

public class RequestProcessingSteps {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private TrainerRepositoryMicro trainerRepositoryMicro;
    @Autowired
    private ConnectionFactory connectionFactory;
    private String trainerUsername;
    private String requestType;
    private String property;

    @Given("the database contains data of trainer's workhours with username {string}")
    public void the_database_contains_a_user_with_username(String trainerUsername) {
        trainerRepositoryMicro.deleteAll();
        List<Trainer.MonthSummary> monthSummaryList = new ArrayList<>();
        monthSummaryList.add(new Trainer.MonthSummary("JANUARY", 70000));
        List<Trainer.YearSummary> workhours = new ArrayList<>();
        Trainer.YearSummary yearSummary = new Trainer.YearSummary();
        yearSummary.setYear(2025);
        yearSummary.setMonthDurationList(monthSummaryList);
        workhours.add(yearSummary);
        Trainer trainer = new Trainer("12345", trainerUsername, TestConstants.TRAINER_FIRST_NAME_AUTHEN, TestConstants.TRAINER_LAST_NAME_AUTHEN, TestConstants.ACTIVE, workhours);
        trainerRepositoryMicro.save(trainer);
    }

    @Given("a message with property {string} set to {string}")
    public void a_message_with_property_set_to(String property, String value) {
        this.property = property;
        this.requestType = value;
    }

    @Given("the message contains username {string}")
    public void the_message_contains_username(String username) {
        this.trainerUsername = username;
    }

    @When("the message is received from the broker queue {string}")
    public void the_message_is_sent_to_the_broker_queue(String queueName) {
        jmsTemplate.convertAndSend(queueName, trainerUsername, msg -> {
            msg.setStringProperty(property, requestType);
            msg.setStringProperty(TRANSACTION_ID, "12345");
            return msg;
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("the microservice should query data base for user {string}")
    public void the_microservice_should_query_db_for_user(String expectedUsername) {
        Optional<Trainer> trainer = trainerRepositoryMicro.findByUsername(expectedUsername);
        assertTrue(trainer.isPresent(), "Trainer should be present in the database");
    }

    @Then("the microservice should send a response to the reply queue {string}")
    public void the_microservice_should_send_a_response_to_the_reply_queue(String replyQueue) throws JMSException {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageConsumer consumer = session.createConsumer(session.createQueue(replyQueue))) {
                Message message = consumer.receive(3000);
                assertNotNull(message, "Response message should be received");
                assertTrue(message instanceof TextMessage, "Response should be a TextMessage");
            }
        }
    }

    @Then("the microservice should throw an exception and message send to DEAD LETTER QUEUE")
    public void the_microservice_should_throw_an_exception() throws JMSException {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageConsumer consumer = session.createConsumer(session.createQueue("DLQ"))) {
                Message message = consumer.receive(3000);
                assertNotNull(message, "Message should be sent in DEAD LETTER QUEUE");
            }
        }
    }

    @Then("the microservice should not send a response to queue {string}")
    public void the_microservice_should_not_send_a_response(String replyQueue) throws JMSException {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageConsumer consumer = session.createConsumer(session.createQueue(replyQueue))) {
                Message message = consumer.receive(3000);
                assertNull(message, "No response message should be sent");
            }
        }
    }
}
