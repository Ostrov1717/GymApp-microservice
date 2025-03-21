package com.example.trainer_work_accounting_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static org.example.shareddto.SharedConstants.TRANSACTION_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSenderServiceMicro {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String queueName, TrainerWorkingHoursDTO dto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(dto);
            jmsTemplate.convertAndSend(queueName, jsonMessage, msg -> {
                msg.setStringProperty(TRANSACTION_ID, MDC.get(TRANSACTION_ID));
                return msg;
            });
            log.info("Information about {} working hours sent to main app", dto.username());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error of serializing TrainerWorkingHoursDTO ", e);
        }
    }
}
