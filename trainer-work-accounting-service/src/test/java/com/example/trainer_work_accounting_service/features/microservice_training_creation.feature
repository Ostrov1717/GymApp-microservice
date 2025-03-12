Feature: Training session creation or deleting

  Scenario: Create training when receiving a "ADD" event
    Given the training event with action "ADD"
    When the message is consumed from the queue
    Then the training should be saved in the database

  Scenario: Delete training when receiving a "DELETE" event
    Given the training event with action "DELETE"
    When the message is consumed from the queue
    Then the training should be removed from the database