package org.example.gym.common;

public final class ApiUrls {
    public static final String V1_BASE = "/api/v1";
    public static final String USER_BASE = V1_BASE + "/user";
    public static final String USER_LOGIN = "/login";
    public static final String USER_CHANGE_LOGIN = "/change-login";
    public static final String TRAINEE_BASE = V1_BASE + "/trainee";
    public static final String GET_TRAINEE_PROFILE = "/profile";
    public static final String REGISTER_TRAINEE = "/registration";
    public static final String UPDATE_TRAINEE = "/update";
    public static final String DELETE_TRAINEE = "/delete";
    public static final String UPDATE_TRAINERS_LIST = "/update-trainers";
    public static final String UPDATE_TRAINEE_STATUS = "/status";
    public static final String TRAINER_BASE = V1_BASE + "/trainer";
    public static final String GET_TRAINER_PROFILE = "/profile";
    public static final String REGISTER_TRAINER = "/registration";
    public static final String UPDATE_TRAINER = "/update";
    public static final String GET_NOT_ASSIGN_TRAINERS = "/not-assign-trainers";
    public static final String UPDATE_TRAINER_STATUS = "/status";
    public static final String TRAININGS_BASE = V1_BASE+"/trainings";
    public static final String GET_TRAINEE_TRAININGS = "/trainee";
    public static final String GET_TRAINER_TRAININGS = "/trainer";
    public static final String CREATE_TRAINING = "/create";
    public static final String TRAINING_TYPES = "/trainings-type";
    public static final String WORKING_HOURS = "/working-hours";
    public static final String ALL_TRAININGS = "/all-trainings";
    public static final String CLIENT_URL = "/api/trainings";

    private ApiUrls() {
    }
}
