package com.sp.sec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.sp.sec.config",
		"com.sp.sec.web"
})
public class ResourceServer1Application {

	public static void main(String[] args) {
		SpringApplication.run(ResourceServer1Application.class, args);
	}

}
