package com.onion.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.onion.backend.adhistory.repository",
        mongoTemplateRef = "mongoTemplate"
)
@EnableMongoAuditing
public class MongoDBConfig {

}
