Feature: Trainer's working hours

  Background:
    Given Trainer with username "Monica.Dobs" works for the gym and her data exists in the system
    And her total training time is:
      | Year | Month    | Duration  |
      | 2024 | DECEMBER | PT15H     |
      | 2025 | JANUARY  | PT5H      |
      | 2025 | FEBRUARY | PT10H     |

  Scenario: Summary of trainers' working hours
    When Trainer "Monica.Dobs" likes to know her summary of total training time and sends an HTTP request
    Then the main application authenticates the coach and sends the request to microservice for working hours of this trainer
    And main app receives total training time from the microservice