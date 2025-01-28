package org.example.gym.domain.trainer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.common.exception.UserNotFoundException;
import org.example.gym.common.exception.WrongTrainingTypeException;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.dto.TrainerMapper;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.trainer.repository.TrainerRepository;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.training.entity.TrainingTypeName;
import org.example.gym.domain.training.repository.TrainingTypeRepository;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.entity.User;
import org.example.gym.domain.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO.Response.Login create(String firstName, String lastName, TrainingTypeName trainingTypeName) {
        log.info("Creating a new Trainer: {} {}", firstName, lastName);
        TrainingType specialization = findTrainingType(trainingTypeName);
        String password = userService.generatePassword();
        String username = userService.generateUserName(firstName, lastName);
        String hashedPassword = passwordEncoder.encode(password);
        Trainer trainer = new Trainer(specialization, new User(firstName, lastName, username, hashedPassword, false));
        trainerRepository.save(trainer);
        log.info("Trainer created with username: {}", trainer.getUser().getUsername());
        return new UserDTO.Response.Login(trainer.getUser().getUsername(), password);
    }

    public TrainerDTO.Response.TrainerProfile findByUsername(String username) {
        Trainer trainer = findTrainerByUsername(username);
        return TrainerMapper.toProfile(trainer);
    }

    public Trainer findTrainerByUsername(String username) {
        log.info("Searching Trainer by username: {}", username);
        return trainerRepository.findByUserUsername(username).orElseThrow(() -> new UserNotFoundException("Trainer with username: " + username + " not found."));
    }

    @Transactional
    public TrainerDTO.Response.TrainerProfile update(String firstName, String lastName, String username, TrainingTypeName trainingTypeName, boolean isActive) {
        log.info("Updating Trainer's data with username: {}", username);
        Trainer trainer = findTrainerByUsername(username);
        TrainingType specialization = findTrainingType(trainingTypeName);
        trainer.getUser().setFirstName(firstName);
        trainer.getUser().setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.getUser().setActive(isActive);
        log.info("Trainer's data with username: {} has been updated", username);
        return TrainerMapper.toProfile(trainer);
    }

    @Transactional
    public Set<TrainerDTO.Response.TrainerSummury> getAvailableTrainers(String traineeUsername) {
        log.info("Search trainers  that not assigned on trainee: {}", traineeUsername);
        Set<Trainer> trainers = trainerRepository.findTrainersNotAssignedToTraineeByUsername(traineeUsername);
        log.info("Found {} active trainers for trainee: {}", trainers.size(), traineeUsername);
        return TrainerMapper.toSetTrainerSummury(trainers);
    }

    @Transactional
    public Set<Trainer> getTrainerFromList(Set<TrainerDTO.Response.TrainerUsername> trainersUsernames) {
        Set<Trainer> newTrainers = new HashSet<>();
        log.info("Forming a list of trainers based on a list of their username");
        for (TrainerDTO.Response.TrainerUsername trainer : trainersUsernames) {
            newTrainers.add(findTrainerByUsername(trainer.getUsername()));
        }
        log.info("List of {} trainers formed", newTrainers.size());
        return newTrainers;
    }

    private TrainingType findTrainingType(TrainingTypeName trainingTypeName) {
        log.info("Comparing the trainer's specialization with the existing ones");
        return trainingTypeRepository.findByTrainingType(trainingTypeName.name())
                .orElseThrow(() -> new WrongTrainingTypeException("Such trainer's specialization not exist in gym"));
    }

    public List<TrainingType> trainingTypes() {
        log.info("Getting available training types");
        return trainingTypeRepository.findAll();
    }

}
