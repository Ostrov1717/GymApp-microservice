package com.example.trainer_work_accounting_service.messaging;

import com.example.trainer_work_accounting_service.TestConstants;
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

import static org.example.shareddto.SharedConstants.NAME_OF_QUEUE_MICROSERVICE_TO_MAIN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageSenderServiceMicroTest {
    @InjectMocks
    private MessageSenderServiceMicro messageSenderServiceMicro;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        messageSenderServiceMicro =
                new MessageSenderServiceMicro(jmsTemplate, objectMapper);
    }

    @Test
    void testSendMessage_Success() throws Exception {
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, new HashMap<>());

        when(objectMapper.writeValueAsString(dto)).thenReturn(TestConstants.JSON_MESSAGE_2);
        doNothing().when(jmsTemplate).convertAndSend(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), eq(TestConstants.JSON_MESSAGE_2), any());

        messageSenderServiceMicro.sendMessage(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN, dto);

        verify(jmsTemplate).convertAndSend(eq(NAME_OF_QUEUE_MICROSERVICE_TO_MAIN), eq(TestConstants.JSON_MESSAGE_2), any());
    }

    @Test
    void testSendMessage_SerializationFailure() throws Exception {
        TrainerWorkingHoursDTO dto = new TrainerWorkingHoursDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, new HashMap<>());

        when(objectMapper.writeValueAsString(dto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> messageSenderServiceMicro.sendMessage(anyString(), dto));
    }
}
