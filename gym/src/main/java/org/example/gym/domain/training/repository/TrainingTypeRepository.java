package org.example.gym.domain.training.repository;

import org.example.gym.domain.training.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RepositoryDefinition(domainClass = TrainingType.class, idClass = Long.class)
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingType(String trainingType);
}
