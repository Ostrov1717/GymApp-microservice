package org.example.gym.domain.trainer.service;


import org.example.gym.common.exception.WrongTrainingTypeException;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.dto.TrainerMapper;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.trainer.repository.TrainerRepository;
import org.example.gym.domain.trainer.service.TrainerService;
import org.example.gym.domain.training.repository.TrainingTypeRepository;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.training.entity.TrainingTypeName;
import org.example.gym.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrainerServiceMicroTests {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserService userservice;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrainerMapper trainerMapper;
    private String firstName = "John";
    private String lastName = "Doe";
    private String username = "John.Doe";
    private String password = "password12";
    TrainingTypeName trainingTypeName = TrainingTypeName.YOGA;
    private TrainingType specialization = new TrainingType(trainingTypeName.name());

    private Trainer BEST_TRAINER = new Trainer(specialization, new User(firstName, lastName, username, password, false));

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Trainer Test")
    @Order(1)
    void createTrainer_success() {
        TrainingTypeName trainingTypeName = TrainingTypeName.YOGA;
        when(trainingTypeRepository.findByTrainingType("YOGA")).thenReturn(Optional.of(specialization));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userservice.generatePassword()).thenReturn(password);
        when(userservice.generateUserName("John", "Doe")).thenReturn("John.Doe");
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        UserDTO.Response.Login result = trainerService.create(firstName, lastName, trainingTypeName);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        verify(userservice, times(1)).generatePassword();
        verify(userservice, times(1)).generateUserName(firstName, lastName);
        verify(trainingTypeRepository, times(1)).findByTrainingType(trainingTypeName.name());
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Create Trainer failed - training type not found")
    @Order(2)
    void createTrainer_trainingTypeNotFound() {
        when(trainingTypeRepository.findByTrainingType(String.valueOf(trainingTypeName))).thenReturn(Optional.empty());

        Exception exception = assertThrows(WrongTrainingTypeException.class, () ->
                trainerService.create(firstName, lastName, trainingTypeName));

        assertEquals("Such trainer's specialization not exist in gym", exception.getMessage());
        verify(trainingTypeRepository, times(1)).findByTrainingType(trainingTypeName.name());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Select Trainer Test by username - success")
    @Order(3)
    void findByUsername_success() {
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(BEST_TRAINER));

        TrainerDTO.Response.TrainerProfile result = trainerService.findByUsername(username);

        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertFalse(result.isActive());
        assertEquals("YOGA", result.getSpecialization().getTrainingType());
        verify(trainerRepository, times(1)).findByUserUsername(username);
    }

    @Test
    @DisplayName("Select Trainer Test by username - not found")
    @Order(4)
    void findByUsername_trainerNotFound() {
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                trainerService.findByUsername(username));

        assertEquals("Trainer with username: " + username + " not found.", exception.getMessage());
        verify(trainerRepository, times(1)).findByUserUsername(username);
    }

    @Test
    @DisplayName("Update Trainer Test - success")
    @Order(5)
    void updateTrainer_success() {
        boolean isActive = true;
        String newFirstName = "Jane";
        String newLastName = "Smith";
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(BEST_TRAINER));
        when(trainingTypeRepository.findByTrainingType(trainingTypeName.name())).thenReturn(Optional.of(specialization));

        TrainerDTO.Response.TrainerProfile updatedProfile = trainerService.update(newFirstName, newLastName, username, trainingTypeName, isActive);

        assertEquals(newFirstName, BEST_TRAINER.getUser().getFirstName());
        assertEquals(newLastName, BEST_TRAINER.getUser().getLastName());
        assertEquals(specialization, BEST_TRAINER.getSpecialization());
        assertTrue(BEST_TRAINER.getUser().isActive(), "The trainer's active status should be updated.");
        verify(trainerRepository, times(1)).findByUserUsername(username);
        verify(trainingTypeRepository, times(1)).findByTrainingType(trainingTypeName.name());
    }

    //
    @Test
    @DisplayName("Getting not assign trainers - successful")
    @Order(6)
    void getAvailableTrainers() {
        try (MockedStatic<TrainerMapper> mockedMapper = (MockedStatic<TrainerMapper>) Mockito.mockStatic(TrainerMapper.class)) {
            TrainingType type1 = new TrainingType("YOGA");
            TrainingType type2 = new TrainingType("FITNESS");
            Trainer trainer2 = new Trainer(type1, new User("Jane", "Doe", "jane.doe", "password", true));
            Trainer trainer1 = new Trainer(type2, new User("Alice", "Smith", "alice.smith", "password", true));
            Set<Trainer> trainers = new HashSet<>(Arrays.asList(trainer1, trainer2));
            TrainerDTO.Response.TrainerSummury trainerSummury1 = new TrainerDTO.Response.TrainerSummury("Jane", "Doe", "jane.doe", type1);
            TrainerDTO.Response.TrainerSummury trainerSummury2 = new TrainerDTO.Response.TrainerSummury("Alice", "Smith", "alice.smith", type2);
            Set<TrainerDTO.Response.TrainerSummury> trainerSummaries = new HashSet<>(Arrays.asList(trainerSummury1, trainerSummury2));
            when(trainerRepository.findTrainersNotAssignedToTraineeByUsername(username)).thenReturn(trainers);
            mockedMapper.when(() -> TrainerMapper.toSetTrainerSummury(trainers))
                    .thenReturn(trainerSummaries);

            Set<TrainerDTO.Response.TrainerSummury> result = trainerService.getAvailableTrainers(username);
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(trainerSummaries, result);
        }
    }

    @Test
    void trainingTypes_shouldReturnAllTrainingTypes() {
        List<TrainingType> expectedTrainingTypes = Arrays.stream(TrainingTypeName.values())
                .map(t -> new TrainingType(t.name()))
                .collect(Collectors.toList());
        when(trainingTypeRepository.findAll()).thenReturn(expectedTrainingTypes);

        List<TrainingType> result = trainerService.trainingTypes();
        assertEquals(expectedTrainingTypes, result);
        verify(trainingTypeRepository, times(1)).findAll();
    }
}

