package org.example.gym.domain.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static org.example.shareddto.SharedConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReceiverService {
    private final ResponseStorageService responseStorageService;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = NAME_OF_QUEUE_MICROSERVICE_TO_MAIN)
    public void receiveMessage(Message message) {
        try {
            String transactionId = message.getStringProperty(TRANSACTION_ID);
            MDC.put(TRANSACTION_ID, transactionId);
            if (message instanceof TextMessage textMessage) {
                String jsonMessage = textMessage.getText();
                TrainerWorkingHoursDTO dto = objectMapper.readValue(jsonMessage, TrainerWorkingHoursDTO.class);
                log.info("Received message from microservice with info about: " + dto.username());
                responseStorageService.storeResponse(dto);
            }
        } catch (Exception e) {
                log.error("Error of deserializing JSON or JMSExseption: " + e.getMessage());
            }
        }

    @JmsListener(destination = DLQ)
    public void receiveDeadQueueLetters(Message message) throws JMSException {
        String transactionId = message.getStringProperty(TRANSACTION_ID);
        MDC.put(TRANSACTION_ID, transactionId);
        if (message instanceof TextMessage textMessage) {
            String jsonMessage = textMessage.getText();
        log.warn("Letter from DEAD LETTER QUEUE !!!");
        }
    }
}
