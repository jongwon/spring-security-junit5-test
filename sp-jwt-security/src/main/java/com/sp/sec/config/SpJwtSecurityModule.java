package com.sp.sec.config;

import com.sp.sec.web.config.SpJwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SpJwtProperties.class})
public class SpJwtSecurityModule {

}
