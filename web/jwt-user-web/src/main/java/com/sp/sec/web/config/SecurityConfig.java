package com.sp.sec.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final SpJwtProperties spJwtProperties;

    private JWTUtil jwtUtil ;

    public SecurityConfig(UserService userService, ObjectMapper objectMapper,
                          SpJwtProperties spJwtProperties) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.spJwtProperties = spJwtProperties;
        this.jwtUtil = new JWTUtil(spJwtProperties);
    }


    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTLoginFilter jwtLoginFilter = new JWTLoginFilter(authenticationManager(), jwtUtil, objectMapper);
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(),
                userService, jwtUtil);
        http
                .csrf().disable()
                .addFilter(jwtLoginFilter)
                .addFilter(checkFilter)
        ;
    }
}
