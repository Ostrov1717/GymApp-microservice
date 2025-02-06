package com.example.trainer_work_accounting_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageReceiverService {

    @JmsListener(destination = "main-to-microservice-queue")
    public void receiveMessage(String message) {
        log.info("Получено сообщение от основного приложения: " + message);
    }
}
