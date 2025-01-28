package org.example.gym.domain.trainee.services;

import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainee.metrics.TraineeMetrics;
import org.example.gym.domain.trainee.repository.TraineeRepository;
import org.example.gym.domain.trainee.service.TraineeService;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TraineeServiceTests {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserService userservice;
    @Mock
    private TraineeMetrics traineeMetrics;
    private String firstName = "John";
    private String lastName = "Doe";
    private String username="John.Doe";
    private String address = "California";
    private String password="password12";
    private LocalDate dob = LocalDate.of(1990, 1, 1);
    private Trainee BEST_TRAINEE = new Trainee(new User(firstName, lastName, username,password, true), address, dob);
    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("Create Trainee Test")
    @Order(1)
    void createTraineeTest() {
        when(traineeRepository.save(any(Trainee.class))).thenReturn(BEST_TRAINEE);
        when(userservice.generatePassword()).thenReturn(password);
        when(userservice.generateUserName("John","Doe")).thenReturn("John.Doe");
        Mockito.doNothing().when(traineeMetrics).incrementNewTrainee();

        UserDTO.Response.Login result = traineeService.create(firstName, lastName, address, dob);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        verify(userservice, times(1)).generatePassword();
        verify(userservice, times(1)).generateUserName(firstName, lastName);
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Select Trainee Test by username - success")
    @Order(2)
    void findByUsername_success() {
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(BEST_TRAINEE));

        TraineeDTO.Response.TraineeProfile result = traineeService.findByUsername(username, password);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertTrue(result.isActive());
        assertEquals("California", result.getAddress());
        assertEquals("1990-01-01", result.getDateOfBirth().toString());
        verify(userservice, times(1)).authenticate(username,password);
        verify(traineeRepository, times(1)).findByUserUsername(username);
    }
    @Test
    @DisplayName("Select Trainee Test by username - not found")
    @Order(3)
    void findByUsername_traineeNotFound() {
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                traineeService.findByUsername(username, password));

        assertEquals("Trainee with username: " + username + " not found.", exception.getMessage());
        verify(userservice, times(1)).authenticate(username, password);
        verify(traineeRepository, times(1)).findByUserUsername(username);
    }
    @Test
    @DisplayName("Update Trainee Test - success")
    @Order(6)
    void updateTrainee_success() {
        String newFirstName = "New name";
        String newLastName = "New Surname";
        String newAddress = "New Address";
        LocalDate newDateOfBirth = LocalDate.of(1990, 2, 2);
        boolean newIsActive = true;
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(BEST_TRAINEE));

        TraineeDTO.Response.TraineeProfileFull result = traineeService.update(newFirstName,newLastName, username, password, newAddress, newDateOfBirth, newIsActive);

        assertEquals(newFirstName, BEST_TRAINEE.getUser().getFirstName());
        assertEquals(newLastName, BEST_TRAINEE.getUser().getLastName());
        assertEquals(newAddress, BEST_TRAINEE.getAddress());
        assertEquals(newDateOfBirth, BEST_TRAINEE.getDateOfBirth());
        assertEquals(newIsActive, BEST_TRAINEE.getUser().isActive());
        verify(userservice, times(1)).authenticate(username, password);
        verify(traineeRepository, times(1)).findByUserUsername(username);
    }
    @Test
    @DisplayName("Delete Trainee Test - success")
    @Order(7)
    void deleteTrainee_success() {
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(BEST_TRAINEE));

        traineeService.delete(username, password);

        verify(userservice, times(1)).authenticate(username, password);
        verify(traineeRepository, times(1)).findByUserUsername(username);
        verify(traineeRepository, times(1)).delete(BEST_TRAINEE);
    }
    @Test
    void updateTraineeTrainers_success() {
        Trainee trainee = new Trainee(new User("John", "Doe", "John.Doe","password12", true),null,null);
        Trainer trainer1 = new Trainer(new TrainingType("FITNESS"),new User("John", "Doe", "John.Doe","password12", true));
        Set<Trainer> assingTrainers= new HashSet<>();
        trainee.setTrainers(assingTrainers);
        Trainer trainer2 = new Trainer(new TrainingType("YOGA"),new User("John", "Doe", "John.Doe","password12", true));
        Set<Trainer> newTrainers = new HashSet<>();
        newTrainers.add(trainer1);
        newTrainers.add(trainer2);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

        Set<TrainerDTO.Response.TrainerSummury> result = traineeService.updateTraineeTrainers(username, password, newTrainers);

        verify(userservice, times(1)).authenticate(username, password);
        verify(traineeRepository, times(1)).save(trainee);
        assertEquals(2, result.size(), "The size of returned trainers should match the new trainers set.");
    }
}
