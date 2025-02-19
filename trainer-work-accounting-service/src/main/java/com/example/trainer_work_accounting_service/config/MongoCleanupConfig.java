package com.example.trainer_work_accounting_service.config;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.Arrays;

@Configuration
@Slf4j
public class MongoCleanupConfig {

    @Bean
    public CommandLineRunner clearCollection(MongoTemplate mongoTemplate) {
        return args -> {
            log.info("Clearing MongoDB collections at startup...");
            for (String collectionName : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.getCollection(collectionName).deleteMany(new Document());
            }
            log.info("MongoDB collections cleared at startup.");

            log.info("Creating indexes for Trainers collection...");
            mongoTemplate.indexOps("trainers").ensureIndex(
                    new Index().on("firstName", Direction.ASC)
                            .on("lastName", Direction.ASC)
            );
            log.info("Indexes created successfully.");
        };
    }
    @Bean
    @DependsOn("clearCollection")
    public CommandLineRunner updateTrainersSchema(MongoTemplate mongoTemplate) {
        return args -> {
            Document schemaDoc = new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("username", "firstName", "lastName", "active", "yearSummaries"))
                    .append("properties", new Document()
                            .append("username", new Document("bsonType", "string"))
                            .append("firstName", new Document("bsonType", "string"))
                            .append("lastName", new Document("bsonType", "string"))
                            .append("active", new Document("bsonType", "bool"))
                            .append("yearSummaries", new Document("bsonType", "array")
                                    .append("items", new Document("bsonType", "object")
                                            .append("properties", new Document()
                                                    .append("year", new Document("bsonType", "int")
                                                            .append("minimum", 2000)
                                                            .append("maximum", 2050))
                                                    .append("monthDurationList", new Document("bsonType", "array")
                                                            .append("maxItems", 12)
                                                            .append("items", new Document("bsonType", "object")
                                                                    .append("properties", new Document()
                                                                            .append("month", new Document("bsonType", "string"))
                                                                            .append("duration", new Document("bsonType", "long")
                                                                                    .append("minimum",600))
                                                                    )))))));
            Document command = new Document("collMod", "trainers")
                    .append("validator", new Document("$jsonSchema", schemaDoc));

            mongoTemplate.getDb().runCommand(command);
            log.info("Updated schema for 'trainers' collection");
        };
    }
}