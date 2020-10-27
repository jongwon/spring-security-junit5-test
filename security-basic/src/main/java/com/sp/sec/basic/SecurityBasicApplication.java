package com.sp.sec.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityBasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityBasicApplication.class, args);
	}

	@Bean
	UserDetailsService users(){
		UserDetails user = User.builder()
				.username("user1")
				.password("1234")
				.roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}


}
