package com.sp.sec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.sp.sec.config",
		"com.sp.sec.web"
})
public class Oauth2ClientTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2ClientTestApplication.class, args);
	}

}
