package com.sp.sec.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {
        "com.sp.sec.user.repository"
})
@ComponentScan("com.sp.sec.user")
public class UserAuthorityModule {


}
