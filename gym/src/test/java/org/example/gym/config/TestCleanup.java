package org.example.gym.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;


public class TestCleanup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeEach
    void cleanUp() {
//        jdbcTemplate.execute("TRUNCATE TABLE trainings RESTART IDENTITY");

        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE) != null) {
        }
    }
}
