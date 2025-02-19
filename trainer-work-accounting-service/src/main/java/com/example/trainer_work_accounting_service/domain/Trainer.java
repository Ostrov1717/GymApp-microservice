package com.example.trainer_work_accounting_service.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "trainers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {
    @Id
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private boolean active;

    private List<YearSummary> yearSummaries;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YearSummary {

        private int year;

        private List<MonthSummary> monthDurationList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthSummary {

        private String month;
        @Min(value=601, message = "Duration must be at least 601 seconds")
        private long duration;
    }
}
