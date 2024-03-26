package com.intellias.distributed.secondtask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.intellias.distributed")
public class MongoConfig {
}
