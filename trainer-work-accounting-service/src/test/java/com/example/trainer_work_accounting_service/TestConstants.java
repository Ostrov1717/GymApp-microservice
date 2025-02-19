package com.example.trainer_work_accounting_service;

import java.time.Duration;
import java.time.LocalDateTime;

public class TestConstants {
    public static String ID = "1";
    public static String USERNAME = "Brad.Pitt";
    public static String FIRST_NAME = "Brad";
    public static String LAST_NAME = "Pitt";
    public static boolean ACTIVE = true;
    public static LocalDateTime TRAINING_DATE = LocalDateTime.of(2025, 1, 30, 9, 15, 0);
    public static Duration DURATION = Duration.ofHours(2);
    public static String JSON_MESSAGE = "{\n" +
            "    \"username\" : \"Brad.Pitt\",\n" +
            "    \"firstName\" : \"Brad\",\n" +
            "    \"lastName\" : \"Pitt\",\n" +
            "    \"active\" : true,\n" +
            "    \"trainingDate\" : \"2025-01-31T09:15:00\",\n" +
            "    \"trainingDuration\" : \"PT2H\",\n" +
            "    \"action\": \"ADD\"\n" +
            "}";

    public static String JSON_MESSAGE_2 = "{\n" +
            "    \"username\": \"Brad.Pitt\",\n" +
            "    \"firstName\": \"Brad\",\n" +
            "    \"lastName\": \"Pitt\",\n" +
            "    \"active\": true,\n" +
            "    \"trainingsDuration\": {\n" +
            "        \"2024\": {\n" +
            "            \"NOVEMBER\": \"PT5H\",\n" +
            "            \"DECEMBER\": \"PT4H\",\n" +
            "            \"OCTOBER\": \"PT4H\",\n" +
            "            \"SEPTEMBER\": \"PT8H\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public static String TRANSACTIONAL_ID = "12345";

}