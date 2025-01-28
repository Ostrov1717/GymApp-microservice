package org.example.gym.domain.trainer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.gym.domain.trainee.entity.Trainee;
import org.example.gym.domain.trainer.dto.TrainerDTO;
import org.example.gym.domain.trainer.dto.TrainerMapper;
import org.example.gym.domain.training.entity.Training;
import org.example.gym.domain.training.entity.TrainingType;
import org.example.gym.domain.user.entity.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"trainees", "trainings"})
@Entity
public class Trainer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", referencedColumnName = "id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Training> trainings = new HashSet<>();

    public Trainer() {
    }

    public Trainer(TrainingType specialization, User user) {
        this.specialization = specialization;
        this.user = user;
    }

    @Override
    public String toString() {
        TrainerDTO.Response.TrainerProfile profile = TrainerMapper.toProfile(this);
        return profile.toString();
    }
}
