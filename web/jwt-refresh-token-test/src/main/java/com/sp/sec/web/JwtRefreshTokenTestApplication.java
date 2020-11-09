package com.sp.sec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.sp.sec.config",
		"com.sp.sec.web"
})
public class JwtRefreshTokenTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtRefreshTokenTestApplication.class, args);
	}

}
