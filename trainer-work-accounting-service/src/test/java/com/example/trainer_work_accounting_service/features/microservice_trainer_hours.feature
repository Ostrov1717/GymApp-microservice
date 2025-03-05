Feature: Request Processing in microservice

  Background:
    Given the database contains data of trainer's workhours with username "Monica.Dobs"

  Scenario: Process valid request from message broker
    Given a message with property "type_of_request" set to "TRAINER_WORKING_HOURS"
    And the message contains username "Monica.Dobs"
    When the message is received from the broker queue "main-to-microservice-request-queue"
    Then the microservice should query data base for user "Monica.Dobs"
    And the microservice should send a response to the reply queue "microservice-to-main-queue"

  Scenario: Process unknown request type
    Given a message with property "type_of_request" set to "UNKNOWN_REQUEST"
    And the message contains username "Monica.Dobs"
    When the message is received from the broker queue "main-to-microservice-request-queue"
    Then the microservice should throw an exception and message send to DEAD LETTER QUEUE
    And the microservice should not send a response to queue "microservice-to-main-queue"