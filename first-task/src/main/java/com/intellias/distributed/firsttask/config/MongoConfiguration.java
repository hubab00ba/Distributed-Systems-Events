package com.intellias.distributed.firsttask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.intellias.distributed")
public class MongoConfiguration {
}
