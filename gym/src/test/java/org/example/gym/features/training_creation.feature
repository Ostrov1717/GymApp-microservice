Feature: Training session creation

  Scenario: Creating a training session
    Given trainer "Monica.Dobs" schedules a training session on "2024-09-25T09:15:00" lasting "PT2H"
    And the workout is called "Evening Yoga"
    And the trainee "Olga.Kurilenko" is assigned to the session
    When the service processes the training session request
    Then the response status should be 200
    And the service sends a message to ActiveMQ with training session details
    And the service stores the training session in the database