package org.example.gym.common;

import org.example.gym.domain.trainee.controller.TraineeControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({TraineeControllerTest.class})
public class ControllersTestSuite {
}
