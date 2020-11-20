package com.sp.sec.user.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class UserOAuth2SupportApp {

    public static void main(String[] args) {
        SpringApplication.run(UserOAuth2SupportApp.class, args);
    }

    @Configuration
    @EnableMongoRepositories(basePackages = {
            "com.sp.sec.user.repository",
            "com.sp.sec.user.oauth2.repository"
    })
    class MongoConfig {}

}
