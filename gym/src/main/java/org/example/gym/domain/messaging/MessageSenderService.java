package org.example.gym.domain.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shareddto.TrainerTrainingDTO;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static org.example.shareddto.SharedConstants.TRANSACTION_ID;
import static org.example.shareddto.SharedConstants.TYPE_OF_REQUEST;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String queueName, TrainerTrainingDTO dto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(dto);
            String transactionId=MDC.get(TRANSACTION_ID)==null?java.util.UUID.randomUUID().toString():MDC.get(TRANSACTION_ID);
            MDC.put(TRANSACTION_ID,transactionId);
            jmsTemplate.convertAndSend(queueName, jsonMessage, msg -> {
                msg.setStringProperty(TRANSACTION_ID, transactionId);
                return msg;
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error of serializing TrainerTrainingDTO", e);
        }
    }

    public void sendRequest(String queueName, String typeOfRequest, String trainerUsername) {
        jmsTemplate.convertAndSend(queueName, trainerUsername, msg -> {
            msg.setStringProperty(TYPE_OF_REQUEST, typeOfRequest);
            msg.setStringProperty(TRANSACTION_ID, MDC.get(TRANSACTION_ID));
            return msg;
        });
    }
}
