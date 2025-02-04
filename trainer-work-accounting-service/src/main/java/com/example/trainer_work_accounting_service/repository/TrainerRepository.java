package com.example.trainer_work_accounting_service.repository;

import com.example.trainer_work_accounting_service.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer,Long> {
    Optional<Trainer> findByUsername(String username);
}