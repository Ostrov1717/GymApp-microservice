package org.example.gym.domain.training.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.gym.common.utils.DurationConverter;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainer.entity.Trainer;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
public class Training implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "trainee_id", referencedColumnName = "traineeid")
    private Trainee trainee;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id", referencedColumnName = "trainerid")
    private Trainer trainer;

    private String trainingName;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "trainingType_id", referencedColumnName = "id")
    private TrainingType trainingType;

    private LocalDateTime trainingDate;
    @Convert(converter = DurationConverter.class)
    private Duration trainingDuration;

    public Training() {
    }

    public Training(Trainee trainee, Trainer trainer, String trainingName, TrainingType trainingType, LocalDateTime trainingDate, Duration trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
