package com.sp.sec.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin(config -> {
                    config.loginPage("/login")
//                    .successForwardUrl("/") // requestCache
                            .failureForwardUrl("/login?error=true");
                })
                .authorizeRequests(config -> {
                    config.antMatchers("/login")
                            .permitAll()
                            .antMatchers("/")
                            .authenticated()
                    ;
                })
        ;
    }
}
