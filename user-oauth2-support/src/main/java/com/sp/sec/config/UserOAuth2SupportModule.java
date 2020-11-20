package com.sp.sec.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan("com.sp.sec.user.oauth2")
@EnableMongoRepositories(basePackages = {
        "com.sp.sec.user.oauth2.repository"
})
public class UserOAuth2SupportModule {
}
