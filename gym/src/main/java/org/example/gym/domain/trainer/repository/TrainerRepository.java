package org.example.gym.domain.trainer.repository;

import org.example.gym.domain.trainer.entity.Trainer;
import org.example.shareddto.TrainerTrainingDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    @EntityGraph(attributePaths = {"trainees", "specialization"})
    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.user.active=true AND t NOT IN (SELECT tr FROM Trainee trainee JOIN trainee.trainers tr WHERE trainee.user.username = :username)")
    Set<Trainer> findTrainersNotAssignedToTraineeByUsername(@Param("username") String username);

    @Query("""
    SELECT new org.example.shareddto.TrainerTrainingDTO(
        u.username,
        u.firstName,
        u.lastName,
        u.active,
        t.trainingDate,
        t.trainingDuration
    )
    FROM Trainer tr
    JOIN tr.user u
    JOIN tr.trainings t
""")
    List<TrainerTrainingDTO> findTrainerTrainings();

}
