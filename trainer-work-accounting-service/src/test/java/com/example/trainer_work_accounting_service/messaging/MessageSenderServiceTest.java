package com.example.trainer_work_accounting_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shareddto.TrainerWorkingHoursDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;

import static com.example.trainer_work_accounting_service.TestConstants.*;
import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageSenderServiceTest {
    @InjectMocks
    private MessageSenderService messageSenderService;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        messageSenderService =
                new MessageSenderService(jmsTemplate, objectMapper);
    }

    @Test
    void testSendMessage_Success() throws Exception {
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, new HashMap<>());

        when(objectMapper.writeValueAsString(dto)).thenReturn(JSON_MESSAGE_2);
        doNothing().when(jmsTemplate).convertAndSend(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), eq(JSON_MESSAGE_2), any());

        messageSenderService.sendMessage(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN, dto);

        verify(jmsTemplate).convertAndSend(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), eq(JSON_MESSAGE_2), any());
    }

    @Test
    void testSendMessage_SerializationFailure() throws Exception {
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, new HashMap<>());

        when(objectMapper.writeValueAsString(dto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> messageSenderService.sendMessage(anyString(), dto));
    }
}
