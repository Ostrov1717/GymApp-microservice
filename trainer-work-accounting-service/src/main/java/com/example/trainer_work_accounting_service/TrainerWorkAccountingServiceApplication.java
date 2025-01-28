package com.example.trainer_work_accounting_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
public class TrainerWorkAccountingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerWorkAccountingServiceApplication.class, args);
	}
}
