package org.example.gym.domain.training.service;


import org.example.gym.domain.messaging.MessageSenderService;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.training.dto.TrainingDTO;
import org.example.gym.domain.training.dto.TrainingMapper;
import org.example.gym.domain.training.entity.Training;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.trainee.repository.TraineeRepository;
import org.example.gym.domain.trainer.repository.TrainerRepository;
import org.example.gym.domain.training.repository.TrainingRepository;
import org.example.gym.domain.training.service.TrainingService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrainingServiceTests {
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private MessageSenderService messageSenderService;
    @InjectMocks
    private TrainingService trainingService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("Create Training Test")
    @Order(1)
    void createTraining_success() {
        String traineeUsername = "trainee";
        String trainerUsername = "trainer";
        String trainingName = "Yoga Class";
        LocalDateTime trainingDate = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Trainee trainee = new Trainee(new User("First", "Trainee", traineeUsername, "password", false), "Address", LocalDate.now());
        Trainer trainer = new Trainer(new TrainingType("Yoga"), new User("First", "Trainer", trainerUsername, "password", false));
        TrainingType trainingType = trainer.getSpecialization();
        Training expectedTraining = new Training(trainee, trainer, trainingName, trainingType, trainingDate, duration);
        when(traineeRepository.findByUserUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(expectedTraining);
        doNothing().when(messageSenderService).sendMessage(anyString(),any());

        trainingService.create(traineeUsername, trainerUsername, trainingName, trainingDate, duration);

        verify(trainingRepository, times(1)).save(argThat(training ->
                training.getTrainee().equals(trainee) &&
                        training.getTrainer().equals(trainer) &&
                        training.getTrainingName().equals(trainingName) &&
                        training.getTrainingDate().equals(trainingDate) &&
                        training.getTrainingDuration().equals(duration)
        ));
    }

    @Test
    @DisplayName("Find trainer list")
    @Order(2)
    void findTrainerList_success() {
        String trainerUsername = "trainerUser";
        LocalDateTime fromDate = LocalDateTime.of(2024, 11, 1, 10, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 11, 30, 18, 0);
        String traineeName = "Trainee Name";
        List<Training> trainings = List.of(
                new Training(new Trainee(new User("John", "Doe", "traineeUser", "password", false), "Address", LocalDate.now()),
                        new Trainer(new TrainingType("YOGA"), new User("Jane", "Smith", "trainerUser", "password", false)),
                        "Yoga Training", new TrainingType("YOGA"), LocalDateTime.now(), Duration.ofHours(1)),
                new Training(new Trainee(new User("Alice", "Wonder", "aliceUser", "password", false), "Address", LocalDate.now()),
                        new Trainer(new TrainingType("FITNESS"), new User("Jane", "Smith", "trainerUser", "password", false)),
                        "Cardio Training", new TrainingType("FITNESS"), LocalDateTime.now(), Duration.ofHours(1))
        );
        when(trainingRepository.findTrainingsByTrainerAndCriteria(trainerUsername, fromDate, toDate, traineeName))
                .thenReturn(trainings);
        try (MockedStatic<TrainingMapper> mockedMapper = mockStatic(TrainingMapper.class)) {
            List<TrainingDTO.Response.TrainingProfileForTrainer> trainingProfiles = List.of(
                    new TrainingDTO.Response.TrainingProfileForTrainer("Yoga Training", LocalDateTime.now(),new TrainingType("YOGA"), Duration.ofHours(1),"traineeUser"),
                    new TrainingDTO.Response.TrainingProfileForTrainer("Cardio Training", LocalDateTime.now(), new TrainingType("FITNESS"),Duration.ofHours(1),"aliceUser")
            );
            mockedMapper.when(() -> TrainingMapper.toListForTrainer(trainings)).thenReturn(trainingProfiles);
            List<TrainingDTO.Response.TrainingProfileForTrainer> result = trainingService.findTrainerList(trainerUsername, fromDate, toDate, traineeName);

            verify(trainingRepository, times(1)).findTrainingsByTrainerAndCriteria(trainerUsername, fromDate, toDate, traineeName);

            mockedMapper.verify(() -> TrainingMapper.toListForTrainer(trainings), times(1));
            assertNotNull(result);
            assertEquals(trainingProfiles.size(), result.size());
            assertTrue(result.stream().anyMatch(profile -> "Yoga Training".equals(profile.getTrainingName())));
            assertTrue(result.stream().anyMatch(profile -> "Cardio Training".equals(profile.getTrainingName())));
        }
    }
    @Test
    @DisplayName("Find trainee list")
    @Order(3)
    void findTraineeList_success() {
        String traineeUsername = "traineeUser";
        LocalDateTime fromDate = LocalDateTime.of(2024, 11, 1, 10, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 11, 30, 18, 0);
        String trainerName = "Trainer Name";
        String trainingType = "YOGA";
        List<Training> trainings = List.of(
                new Training(new Trainee(new User("John", "Doe", "traineeUser", "password", false), "Address", LocalDate.now()),
                        new Trainer(new TrainingType("Yoga"), new User("Jane", "Smith", "trainerUser", "password", false)),
                        "Yoga Training", new TrainingType("Yoga"), LocalDateTime.now(), Duration.ofHours(1)),
                new Training(new Trainee(new User("Alice", "Wonder", "aliceUser", "password", false), "Address", LocalDate.now()),
                        new Trainer(new TrainingType("Cardio"), new User("Jane", "Smith", "trainerUser", "password", false)),
                        "Cardio Training", new TrainingType("Cardio"), LocalDateTime.now(), Duration.ofHours(1))
        );

        when(trainingRepository.findTrainingsByTraineeAndCriteria(traineeUsername, fromDate, toDate, trainerName))
                .thenReturn(trainings);

        try (MockedStatic<TrainingMapper> mockedMapper = mockStatic(TrainingMapper.class)) {
            List<TrainingDTO.Response.TrainingProfileForTrainee> trainingProfiles = List.of(
                    new TrainingDTO.Response.TrainingProfileForTrainee("Yoga Training",  LocalDateTime.now(), new TrainingType("YOGA"),Duration.ofHours(1),"trainerUser"),
                    new TrainingDTO.Response.TrainingProfileForTrainee("Cardio Training",  LocalDateTime.now(), new TrainingType("FITNESS"), Duration.ofHours(1),"trainerUser")
            );
            mockedMapper.when(() -> TrainingMapper.toListForTrainee(trainings)).thenReturn(trainingProfiles);

            List<TrainingDTO.Response.TrainingProfileForTrainee> result = trainingService.findTraineeList(traineeUsername, fromDate, toDate, trainerName, trainingType);

            verify(trainingRepository, times(1)).findTrainingsByTraineeAndCriteria(traineeUsername, fromDate, toDate, trainerName);
            mockedMapper.verify(() -> TrainingMapper.toListForTrainee(trainings), times(1));
            assertNotNull(result);
            assertEquals(trainingProfiles.size(), result.size());
            assertTrue(result.stream().anyMatch(profile -> "Yoga Training".equals(profile.getTrainingName())));
            assertTrue(result.stream().anyMatch(profile -> "Cardio Training".equals(profile.getTrainingName())));
        }
    }
}
