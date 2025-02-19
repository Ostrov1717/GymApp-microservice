package com.example.trainer_work_accounting_service.messaging;

import com.example.trainer_work_accounting_service.service.TrainerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.example.shareddto.ActionType;
import org.example.shareddto.TrainerTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static com.example.trainer_work_accounting_service.TestConstants.*;
import static org.example.shareddto.SharedConstants.TYPE_OF_REQUEST_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageReceiverServiceTest {
    @InjectMocks
    private MessageReceiverService messageReceiverService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        messageReceiverService =
                new MessageReceiverService(trainerService, objectMapper);
    }

    @Test
    void receiveMessageOneTraining_ShouldProcessAddAction() throws Exception {
        TrainerTrainingDTO dto = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, DURATION, ActionType.ADD);

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(JSON_MESSAGE);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TRANSACTIONAL_ID);
        when(objectMapper.readValue(JSON_MESSAGE, TrainerTrainingDTO.class)).thenReturn(dto);

        messageReceiverService.receiveMessageOneTraining(textMessage);

        assertEquals(TRANSACTIONAL_ID, MDC.get("transactionId"));
        verify(trainerService).addTraining(dto);
    }

    @Test
    void receiveMessageOneTraining_ShouldProcessDeleteAction() throws Exception {
        TrainerTrainingDTO dto = new TrainerTrainingDTO(USERNAME, FIRST_NAME, LAST_NAME, ACTIVE, TRAINING_DATE, DURATION, ActionType.DELETE);

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(JSON_MESSAGE);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TRANSACTIONAL_ID);
        when(objectMapper.readValue(JSON_MESSAGE, TrainerTrainingDTO.class)).thenReturn(dto);

        messageReceiverService.receiveMessageOneTraining(textMessage);

        assertEquals(TRANSACTIONAL_ID, MDC.get("transactionId"));
        verify(trainerService).deleteTraining(dto);
    }

    @Test
    void receiveMessageOneTraining_ShouldHandleJsonException() throws Exception {
        String jsonMessage = "invalid_json";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(jsonMessage);
        when(objectMapper.readValue(jsonMessage, TrainerTrainingDTO.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {
                });

        assertDoesNotThrow(() -> messageReceiverService.receiveMessageOneTraining(textMessage));
        verify(trainerService, never()).addTraining(any());
    }

    @Test
    void testReceiveMainAppRequest_validRequest() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(USERNAME);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TRANSACTIONAL_ID);
        when(textMessage.getStringProperty("type_of_request")).thenReturn(TYPE_OF_REQUEST_1);
        doNothing().when(trainerService).getTrainingSummaryByUsername(USERNAME);

        messageReceiverService.receiveMainAppRequest(textMessage);

        verify(trainerService).getTrainingSummaryByUsername(USERNAME);
    }

    @Test
    void testReceiveMainAppRequest_invalidRequestType() throws Exception {
        String type = "INVALID_REQUEST_TYPE";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(USERNAME);
        when(textMessage.getStringProperty("transactionId")).thenReturn(TRANSACTIONAL_ID);
        when(textMessage.getStringProperty("type_of_request")).thenReturn(type);

        assertThrows(IllegalArgumentException.class, () -> messageReceiverService.receiveMainAppRequest(textMessage));
    }

    @Test
    void testReceiveMainAppRequest_jmsException() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenThrow(new JMSException("Simulated JMSException"));

        assertDoesNotThrow(() -> messageReceiverService.receiveMainAppRequest(textMessage));
    }
}

