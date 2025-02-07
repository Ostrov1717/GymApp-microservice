package org.example.gym.domain.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shareddto.TrainerTrainingDTO;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String queueName, TrainerTrainingDTO dto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(dto);
            String transactionId=MDC.get("transactionId")==null?java.util.UUID.randomUUID().toString():MDC.get("transactionId");
            MDC.put("transactionId",transactionId);
            jmsTemplate.convertAndSend(queueName, jsonMessage, msg -> {
                msg.setStringProperty("transactionId", transactionId);
                return msg;
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error of serializing TrainerTrainingDTO", e);
        }
    }

    public void sendRequest(String queueName, String typeOfRequest, String trainerUsername) {
        jmsTemplate.convertAndSend(queueName, trainerUsername, msg -> {
            msg.setStringProperty("type_of_request", typeOfRequest);
            msg.setStringProperty("transactionId", MDC.get("transactionId"));
            return msg;
        });
    }
}
