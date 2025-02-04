package com.example.trainer_work_accounting_service.domain;

import com.example.trainer_work_accounting_service.converters.MonthConverter;
import com.example.trainer_work_accounting_service.converters.YearConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Month;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonthSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_summary_id")
    private YearSummary yearSummary;

    @Convert(converter = MonthConverter.class)
    @Column(name = "month_id")
    private Month month;

    private Duration duration;
}
