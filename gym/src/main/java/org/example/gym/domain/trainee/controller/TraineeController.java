package org.example.gym.domain.trainee.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gym.domain.trainee.dto.TraineeDTO;
import org.example.gym.domain.trainee.service.TraineeService;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.entity.Trainer;
import org.example.gym.domain.trainer.service.TrainerService;
import org.example.gym.domain.user.dto.UserDTO;
import org.example.gym.domain.user.service.UserService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Set;

import static org.example.gym.common.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(TRAINEE_BASE)
public class TraineeController {
    private final TraineeService traineeService;
    private final UserService userService;
    private final TrainerService trainerService;

    //    1. Trainee Registration (POST method)
    @PostMapping(REGISTER_TRAINEE)
    public UserDTO.Response.Login traineeRegistration(@Valid @RequestBody TraineeDTO.Request.TraineeRegistration dto) {
        log.info("POST request to " + REGISTER_TRAINEE + " for registration trainee with details: {} {}, date of birth={}, address={}"
                , dto.getFirstName(), dto.getLastName(), dto.getDateOfBirth(), dto.getAddress());
        return traineeService.create(dto.getFirstName(), dto.getLastName(), dto.getAddress(), dto.getDateOfBirth());
    }

    //  5. Get Trainee Profile (GET method
    @GetMapping(GET_TRAINEE_PROFILE)
    public TraineeDTO.Response.TraineeProfile getTraineeProfile() {
        log.info("GET request to " + GET_TRAINEE_PROFILE + " with trainee username={}", getAuthenticatedUsername());
        return traineeService.findByUsername(getAuthenticatedUsername());
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    //  6. Update Trainee Profile (PUT method)
    @PutMapping(UPDATE_TRAINEE)
    public TraineeDTO.Response.TraineeProfileFull updateTraineeProfile(@Valid @RequestBody TraineeDTO.Request.TraineeUpdate dto) {
        log.info("PUT request to " + UPDATE_TRAINEE + " trainee={} with new details: {} {}, date of birth={}, address={}",
                getAuthenticatedUsername(), dto.getFirstName(), dto.getLastName(), dto.getAddress(), dto.getDateOfBirth());
        return traineeService.update(dto.getFirstName(), dto.getLastName(), getAuthenticatedUsername(),
                dto.getAddress(), dto.getDateOfBirth(), dto.isActive());
    }

    //  7. Delete Trainee Profile (DELETE method)
    @DeleteMapping(DELETE_TRAINEE)
    public void deleteTraineeProfile() {
        log.info("DELETE request to " + DELETE_TRAINEE + " trainee={}", getAuthenticatedUsername());
        traineeService.delete(getAuthenticatedUsername());
    }

    //    11. Update Trainee's Trainer List (PUT method)
    @PutMapping(UPDATE_TRAINERS_LIST)
    public Set<TrainerDTO.Response.TrainerSummury> updateTraineeTrainers(@Valid @RequestBody TraineeDTO.Request.UpdateTrainers dto) {
        log.info("PUT request to " + UPDATE_TRAINERS_LIST + " for trainee={} with new trainers: {}",
                getAuthenticatedUsername(), dto.getTrainersUsernames());
        Set<Trainer> newTrainers = trainerService.getTrainerFromList(dto.getTrainersUsernames());
        return traineeService.updateTraineeTrainers(getAuthenticatedUsername(), newTrainers);
    }

    //    15. Activate/De-Activate Trainee (PATCH method)
    @PatchMapping(UPDATE_TRAINEE_STATUS)
    public void activateOrDeactivateTrainee(@Valid @RequestBody UserDTO.Request.ActivateOrDeactivate dto) {
        log.info("PATCH request to " + UPDATE_TRAINEE_STATUS + " trainee={} with updates active status on: {}", getAuthenticatedUsername(), dto.isActive());
        if (dto.isActive()) {
            userService.activate(getAuthenticatedUsername());
        } else {
            userService.deactivate(getAuthenticatedUsername());
        }
    }

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, "dateOfBirth", new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }
}
