package org.example.gym.domain.training.repository;

import org.example.gym.domain.training.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
@Query("""
    SELECT t FROM Training t
    WHERE t.trainer.user.username = :trainerUsername
    AND t.trainingDate >= :fromDate
    AND t.trainingDate <= :toDate
    AND (:traineeUsername IS NULL OR t.trainee.user.username = :traineeUsername)
""")
    List<Training> findTrainingsByTrainerAndCriteria(
            @Param("trainerUsername") String username,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("traineeUsername") String traineeName);

    @Query("""
    SELECT t FROM Training t
    WHERE t.trainee.user.username = :traineeUsername
    AND t.trainingDate >= :fromDate
    AND t.trainingDate <= :toDate
    AND (:trainerUsername IS NULL OR t.trainer.user.username = :trainerUsername)
""")
    List<Training> findTrainingsByTraineeAndCriteria(
            @Param("traineeUsername") String username,
            @Param("fromDate") LocalDateTime periodFrom,
            @Param("toDate") LocalDateTime periodTo,
            @Param("trainerUsername") String trainerName);
}
