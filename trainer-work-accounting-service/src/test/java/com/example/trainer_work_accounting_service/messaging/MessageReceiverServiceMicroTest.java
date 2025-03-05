package com.example.trainer_work_accounting_service.messaging;

import com.example.trainer_work_accounting_service.TestConstants;
import com.example.trainer_work_accounting_service.service.TrainerServiceMicro;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.example.shareddto.SharedConstants.TYPE_OF_REQUEST_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageReceiverServiceMicroTest {
    @InjectMocks
    private MessageReceiverServiceMicro messageReceiverServiceMicro;
    @Mock
    private TrainerServiceMicro trainerServiceMicro;
    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        messageReceiverServiceMicro =
                new MessageReceiverServiceMicro(trainerServiceMicro, objectMapper);
    }

    @Test
    void receiveMessageOneTraining_ShouldProcessAddAction() throws Exception {
        TrainerTrainingDTO dto = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.ADD);

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(TestConstants.JSON_MESSAGE);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TestConstants.TRANSACTIONAL_ID);
        when(objectMapper.readValue(TestConstants.JSON_MESSAGE, TrainerTrainingDTO.class)).thenReturn(dto);

        messageReceiverServiceMicro.receiveMessageOneTraining(textMessage);

        Assertions.assertEquals(TestConstants.TRANSACTIONAL_ID, MDC.get("transactionId"));
        verify(trainerServiceMicro).addTraining(dto);
    }

    @Test
    void receiveMessageOneTraining_ShouldProcessDeleteAction() throws Exception {
        TrainerTrainingDTO dto = new TrainerTrainingDTO(TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE, TestConstants.TRAINING_DATE, TestConstants.DURATION, ActionType.DELETE);

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(TestConstants.JSON_MESSAGE);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TestConstants.TRANSACTIONAL_ID);
        when(objectMapper.readValue(TestConstants.JSON_MESSAGE, TrainerTrainingDTO.class)).thenReturn(dto);

        messageReceiverServiceMicro.receiveMessageOneTraining(textMessage);

        Assertions.assertEquals(TestConstants.TRANSACTIONAL_ID, MDC.get("transactionId"));
        verify(trainerServiceMicro).deleteTraining(dto);
    }

    @Test
    void receiveMessageOneTraining_ShouldHandleJsonException() throws Exception {
        String jsonMessage = "invalid_json";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(jsonMessage);
        when(objectMapper.readValue(jsonMessage, TrainerTrainingDTO.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {
                });

        assertDoesNotThrow(() -> messageReceiverServiceMicro.receiveMessageOneTraining(textMessage));
        verify(trainerServiceMicro, never()).addTraining(any());
    }

    @Test
    void testReceiveMainAppRequest_validRequest() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(TestConstants.USERNAME);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TestConstants.TRANSACTIONAL_ID);
        when(textMessage.getStringProperty("type_of_request")).thenReturn(TYPE_OF_REQUEST_1);
        doNothing().when(trainerServiceMicro).getTrainingSummaryByUsername(TestConstants.USERNAME);

        messageReceiverServiceMicro.receiveMainAppRequest(textMessage);

        verify(trainerServiceMicro).getTrainingSummaryByUsername(TestConstants.USERNAME);
    }

    @Test
    void testReceiveMainAppRequest_invalidRequestType() throws Exception {
        String type = "INVALID_REQUEST_TYPE";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(TestConstants.USERNAME);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TestConstants.TRANSACTIONAL_ID);
        when(textMessage.getStringProperty("type_of_request")).thenReturn(type);

        assertThrows(IllegalArgumentException.class, () -> messageReceiverServiceMicro.receiveMainAppRequest(textMessage));
    }

    @Test
    void testReceiveMainAppRequest_jmsException() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenThrow(new JMSException("Simulated JMSException"));

        assertDoesNotThrow(() -> messageReceiverServiceMicro.receiveMainAppRequest(textMessage));
    }
}

