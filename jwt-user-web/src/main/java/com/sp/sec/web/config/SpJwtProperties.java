package com.sp.sec.web.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sp.jwt")
public class SpJwtProperties {

    private String secret = "default-secret-value";
    private long tokenLifeTime = 600;

}
