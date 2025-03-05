package com.example.trainer_work_accounting_service.repository;

import com.example.trainer_work_accounting_service.domain.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TrainerRepositoryMicro extends MongoRepository<Trainer,String> {
    Optional<Trainer> findByUsername(String username);
}