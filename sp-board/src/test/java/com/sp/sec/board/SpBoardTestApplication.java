package com.sp.sec.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class SpBoardTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpBoardTestApplication.class, args);
    }

    @Profile("board-test")
    @Configuration
    @EnableMongoRepositories(basePackages = {
            "com.sp.sec.user.repository",
            "com.sp.sec.board.repository"
    })
    class MongoConfig{

    }

}
