package com.example.trainer_work_accounting_service.config;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ArtemisTestConfig {

    @Bean
    public EmbeddedActiveMQ embeddedActiveMQ() throws Exception {
        EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
        Configuration config = new ConfigurationImpl()
                .setPersistenceEnabled(false)
                .setSecurityEnabled(false)
                .addAcceptorConfiguration("vm", "vm://embedded");
        embeddedActiveMQ.setConfiguration(config);
        embeddedActiveMQ.start();
        return embeddedActiveMQ;
    }
}
