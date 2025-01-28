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
    @Query(value="""
        SELECT t
        FROM training_list t
        WHERE (t.trainer_username = :username)
          AND (:fromDate IS NULL OR t.training_date >= :fromDate)
          AND (:toDate IS NULL OR t.training_date <= :toDate)
          AND (:traineeName IS NULL OR t.trainee_username LIKE %:traineeName%)
    """, nativeQuery = true)
    List<Training> findTrainingsByTrainerAndCriteria(
            @Param("username") String username,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("traineeName") String traineeName);

    @Query(value="""
        SELECT t
        FROM training_list t
        WHERE (t.trainee_username = :username)
          AND (:fromDate IS NULL OR t.training_date >= :fromDate)
          AND (:toDate IS NULL OR t.training_date <= :toDate)
          AND (:trainerName IS NULL OR t.trainer_username LIKE %:trainerName%)
    """,nativeQuery = true)
    List<Training> findTrainingsByTraineeAndCriteria(
            @Param("username") String username,
            @Param("fromDate") LocalDateTime periodFrom,
            @Param("toDate") LocalDateTime periodTo,
            @Param("trainerName") String trainerName);
}
