package com.example.trainer_work_accounting_service.messaging;

import com.example.trainer_work_accounting_service.service.TrainerServiceMicro;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerTrainingDTO;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static org.example.shareddto.SharedConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReceiverServiceMicro {
    private final TrainerServiceMicro trainerServiceMicro;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = NAME_OF_QUEUE_MAIN_TO_MICROSERVICE)
    public void receiveMessageOneTraining(Message message) {
        try {
            String transactionId = message.getStringProperty(TRANSACTION_ID);
            MDC.put(TRANSACTION_ID, transactionId);
            if (message instanceof TextMessage textMessage) {
                String jsonMessage = textMessage.getText();
                TrainerTrainingDTO dto = objectMapper.readValue(jsonMessage, TrainerTrainingDTO.class);
                log.info("Received message from main application (one training): " + dto);
                switch (dto.action()) {
                    case ADD -> trainerServiceMicro.addTraining(dto);
                    case DELETE -> trainerServiceMicro.deleteTraining(dto);
                }
            }
        } catch (Exception e) {
            log.error("Error processing received message: " + e.getMessage());
        }
    }

    @JmsListener(destination = NAME_OF_QUEUE_MAIN_TO_MICROSERVICE_REQUEST)
    public void receiveMainAppRequest(Message message) {
        try {
            String transactionId = message.getStringProperty(TRANSACTION_ID);
            String type = message.getStringProperty(TYPE_OF_REQUEST);
            MDC.put(TRANSACTION_ID, transactionId);
            if (message instanceof TextMessage textMessage) {
                String trainerUsername = textMessage.getText();
                log.info("Received request from main application about: " + trainerUsername+", request type:"+type);
                if (type.equals(TYPE_OF_REQUEST_1)) {
                    trainerServiceMicro.getTrainingSummaryByUsername(trainerUsername);
                } else {
                    throw new IllegalArgumentException("No such service in microservice!");
                }
            }
        } catch (JMSException e) {
            log.error("Error processing received message: " + e.getMessage());
        }
    }
}
