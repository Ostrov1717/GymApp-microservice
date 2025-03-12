package org.example.gym.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MAIN_TO_MICROSERVICE;

@Component
public class TestCleanup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeEach
    public void cleanUp() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC'", String.class);

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
        }

        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(NAME_OF_QUEUE_MAIN_TO_MICROSERVICE) != null) {
        }
    }
}
