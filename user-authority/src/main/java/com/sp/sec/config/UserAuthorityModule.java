package com.sp.sec.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan("com.sp.sec.user")
@EnableMongoRepositories(basePackages = {
        "com.sp.sec.user.repository"
})
public class UserAuthorityModule {


}
