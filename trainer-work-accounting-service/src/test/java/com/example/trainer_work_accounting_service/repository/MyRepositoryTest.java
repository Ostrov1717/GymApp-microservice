package com.example.trainer_work_accounting_service.repository;


import com.example.trainer_work_accounting_service.TestConstants;
import com.example.trainer_work_accounting_service.domain.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MyRepositoryTest {
    @Autowired
    private TrainerRepositoryMicro myRepository;

    @Test
    void testSaveAndFind() {
        Trainer trainer = new Trainer("12345", TestConstants.USERNAME, TestConstants.FIRST_NAME, TestConstants.LAST_NAME, TestConstants.ACTIVE,new ArrayList<>());
        myRepository.save(trainer);
        List<Trainer> result = myRepository.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getUsername()).isEqualTo(TestConstants.USERNAME);
    }
}
