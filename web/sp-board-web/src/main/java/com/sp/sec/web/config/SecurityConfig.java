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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final SpJwtProperties properties;
    private final JWTUtil jwtUtil;

    public SecurityConfig(UserService userService, ObjectMapper objectMapper, SpJwtProperties properties) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.properties = properties;
        jwtUtil = new JWTUtil(properties);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final JWTLoginFilter loginFilter = new JWTLoginFilter(authenticationManager(), jwtUtil, objectMapper);
        final JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(), userService, jwtUtil);

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(loginFilter)
                .addFilter(checkFilter)
        ;
    }
}
