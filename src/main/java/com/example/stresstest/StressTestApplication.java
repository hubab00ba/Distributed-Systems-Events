package com.example.stresstest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class StressTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(StressTestApplication.class, args);
	}

}
