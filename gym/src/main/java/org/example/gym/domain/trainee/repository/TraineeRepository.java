package org.example.gym.domain.trainee.repository;

import org.example.gym.domain.trainee.entity.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    @EntityGraph(attributePaths = "trainers")
    Optional<Trainee> findByUserUsername(String username);
}
