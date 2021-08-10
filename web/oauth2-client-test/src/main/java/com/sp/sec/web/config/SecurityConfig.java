package com.sp.sec.web.config;

import com.sp.sec.user.oauth2.service.ExtendedUserService;
import com.sp.sec.web.service.SpOAuth2UserService;
import com.sp.sec.web.service.SpOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SpOAuth2UserService oAuth2UserService;

    @Autowired
    private SpOidcUserService oidcUserService;

    @Autowired
    private ExtendedUserService extendedUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(extendedUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .and()
                .oauth2Login(oauth2->{
                    oauth2.userInfoEndpoint(userinfo->{
                        userinfo.userService(oAuth2UserService) // OAuth2 방식
                                .oidcUserService(oidcUserService) // OIDC 방식
                                ;
                    });
                })
                ;
    }
}
