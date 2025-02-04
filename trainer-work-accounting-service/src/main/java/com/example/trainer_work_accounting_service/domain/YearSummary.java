package com.example.trainer_work_accounting_service.domain;

import com.example.trainer_work_accounting_service.converters.YearConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class YearSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Convert(converter = YearConverter.class)
    @Column(name = "year_id")
    private Year year;

    @OneToMany(mappedBy = "yearSummary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonthSummary> monthDurationList=new ArrayList<>();
}