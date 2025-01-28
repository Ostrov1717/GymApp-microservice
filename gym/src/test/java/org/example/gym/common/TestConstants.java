package org.example.gym.common;

import org.example.gym.domain.training.entity.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestConstants {
    public static String FIRST_NAME = "Brad";
    public static String LAST_NAME = "Pitt";
    public static LocalDate DATE_OF_BIRTH = LocalDate.parse("1990-01-01");
    public static String ADDRESS = "02055, California, Mulholland Drive";
    public static String USERNAME = "Brad.Pitt";
    public static String PASSWORD = "12345";
    public static TrainingType SPECIALIZATION = new TrainingType("RESISTANCE");
    public static String TRAINEE_USERNAME = "Olga.Kurilenko";
    public static String TRAINER_USERNAME = "Monica.Dobs";
    public static LocalDateTime PERIOD_FROM = LocalDateTime.of(2024, 1, 1, 9, 15);
    public static LocalDateTime PERIOD_TO = LocalDateTime.of(2024, 12, 31, 9, 45);
    public static TrainingType TRAINING_TYPE = new TrainingType("FITNESS");
    public static String TRAINING_NAME = "Evening jogging";
    public static Duration DURATION = Duration.ofHours(1);
    public static String INVALID_USERNAME = "";
    public static String INVALID_PASSWORD = "";
    public static String NEW_PASSWORD = "12345";
}