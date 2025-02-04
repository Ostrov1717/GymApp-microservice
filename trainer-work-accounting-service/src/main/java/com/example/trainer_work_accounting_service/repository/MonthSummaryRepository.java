package com.example.trainer_work_accounting_service.repository;

import com.example.trainer_work_accounting_service.domain.MonthSummary;
import com.example.trainer_work_accounting_service.domain.YearSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Month;
import java.util.Optional;

public interface MonthSummaryRepository extends JpaRepository<MonthSummary,Long> {
    Optional<MonthSummary> findByYearSummaryAndMonth(YearSummary yearSummary, Month month);
    long countByYearSummary(YearSummary yearSummary);
}
