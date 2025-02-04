package com.example.trainer_work_accounting_service.repository;

import com.example.trainer_work_accounting_service.domain.Trainer;
import com.example.trainer_work_accounting_service.domain.YearSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface YearSummaryRepository extends JpaRepository<YearSummary,Long> {
   Optional<YearSummary> findByTrainerAndYear(Trainer trainer, Year year);
   List<YearSummary> findByTrainer(Trainer trainer);
}