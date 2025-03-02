package org.example.shareddto;

public final class SharedConstants {
    public static final String NAME_OF_QUEUE_MICROSERVICE_TO_MAIN = "microservice-to-main-queue";
    public static final String NAME_OF_QUEUE_MAIN_TO_MICROSERVICE = "main-to-microservice-queue";
    public static final String NAME_OF_QUEUE_MAIN_TO_MICROSERVICE_REQUEST = "main-to-microservice-request-queue";
    public static final String TYPE_OF_REQUEST_1 = "TRAINER_WORKING_HOURS";
    public static final String TYPE_OF_REQUEST_2 = "TRAINER_QUONTITY_OF_TRAININGS";
    public static final String DLQ = "DLQ";
    public static final String TRANSACTION_ID = "transactionId";

    public static final String TYPE_OF_REQUEST = "type_of_request";

    private SharedConstants(){}
}
