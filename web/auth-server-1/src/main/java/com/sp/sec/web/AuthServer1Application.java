package com.sp.sec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.sp.sec.config",
		"com.sp.sec.web"
})
public class AuthServer1Application {

	public static void main(String[] args) {
		SpringApplication.run(AuthServer1Application.class, args);
	}

}
