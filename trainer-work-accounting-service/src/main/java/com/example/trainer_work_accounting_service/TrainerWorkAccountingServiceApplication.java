package com.example.trainer_work_accounting_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class TrainerWorkAccountingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerWorkAccountingServiceApplication.class, args);
	}
}
