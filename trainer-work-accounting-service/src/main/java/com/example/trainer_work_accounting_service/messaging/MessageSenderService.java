package org.example.gym.domain.messaging;

import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final JmsTemplate jmsTemplate;
    private final Queue mainToMicroserviceQueue;

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(mainToMicroserviceQueue, message);
    }
}
