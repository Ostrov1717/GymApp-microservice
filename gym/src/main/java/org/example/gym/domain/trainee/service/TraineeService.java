package org.example.gym.domain.trainee.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.trainee.dto.TraineeMapper;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainee.metrics.TraineeMetrics;
import org.example.gym.domain.trainee.repository.TraineeRepository;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.dto.TrainerMapper;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserService userservice;
    private final TraineeMetrics traineeMetrics;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO.Response.Login create(String firstName, String lastName, String address, LocalDate dateOfBirth) {
        log.info("Creating a new Trainee: {} {}", firstName, lastName);
        String password = userservice.generatePassword();
        String username = userservice.generateUserName(firstName, lastName);
        String hashedPassword = passwordEncoder.encode(password);
        Trainee trainee = new Trainee(new User(firstName, lastName, username, hashedPassword, false), address, dateOfBirth);
        traineeRepository.save(trainee);
        log.info("Trainee created with username: {}", trainee.getUser().getUsername());
        this.traineeMetrics.incrementNewTrainee();
        return new UserDTO.Response.Login(trainee.getUser().getUsername(), password);
    }

    public TraineeDTO.Response.TraineeProfile findByUsername(String username) {
        log.info("Searching Trainee by username: {}", username);
        Trainee trainee = findTraineeByUsername(username);
        TraineeDTO.Response.TraineeProfile traineeProfile = TraineeMapper.toProfile(trainee);
        return traineeProfile;
    }

    private Trainee findTraineeByUsername(String username) {
        return traineeRepository.findByUserUsername(username).orElseThrow(() -> new UserNotFoundException("Trainee with username: " + username + " not found."));
    }

    @Transactional
    public TraineeDTO.Response.TraineeProfileFull update(String firstName, String lastName, String username, String address, LocalDate dateOfBirth, boolean isActive) {
        log.info("Updating Trainee's data with username: {}", username);
        Trainee trainee = findTraineeByUsername(username);
        trainee.getUser().setFirstName(firstName);
        trainee.getUser().setLastName(lastName);
        trainee.getUser().setActive(isActive);
        trainee.setAddress(address);
        trainee.setDateOfBirth(dateOfBirth);
        log.info("Trainee's data with username: {} has been updated", username);
        return TraineeMapper.toProfileFull(trainee);
    }

    @Transactional
    public void delete(String username) {
        log.info("Deleting Trainee with username: {}", username);
        Trainee trainee = findTraineeByUsername(username);
        traineeRepository.delete(trainee);
        log.info("Trainee with username: {} successfully deleted", username);
    }

    @Transactional
    public Set<TrainerDTO.Response.TrainerSummury> updateTraineeTrainers(String username, Set<Trainer> newTrainers) {
        Trainee trainee = findTraineeByUsername(username);
        log.info("Updating trainers for trainee with username: {}", username);
        log.info("Current number of trainers: {}", trainee.getTrainers().size());
        trainee.setTrainers(newTrainers);
        traineeRepository.save(trainee);
        log.info("Trainers for trainee with username {} have been successfully updated. New number of trainers: {}", username, newTrainers.size());
        return TrainerMapper.toSetTrainerSummury(trainee.getTrainers());
    }
}
