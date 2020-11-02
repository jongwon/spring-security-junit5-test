package com.sp.sec.web;

import com.sp.sec.web.config.SpJwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {
		"com.sp.sec.config",
		"com.sp.sec.web"
})
public class JwtUserWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtUserWebApplication.class, args);
	}

}
