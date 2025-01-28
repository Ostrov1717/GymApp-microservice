package org.example.gym.common.exception;

public class WrongTrainingTypeException extends RuntimeException{

    public WrongTrainingTypeException(String message){
        super(message);
    }
}