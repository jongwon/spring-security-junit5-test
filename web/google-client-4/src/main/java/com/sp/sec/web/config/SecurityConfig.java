package com.sp.sec.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.client.web.server.authentication.OAuth2LoginAuthenticationWebFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    CommonOAuth2Provider provider;

    @Autowired
    private SpGoogleUser spGoogleUser;

    @Autowired
    private SpGoogleUserToMyUserFilter googleUserToMyUserFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .oauth2Login(oauth->{
                    oauth.userInfoEndpoint(userinfo->{
                        userinfo.oidcUserService(spGoogleUser);
                    });
                })
                .addFilterAfter(googleUserToMyUserFilter, OAuth2LoginAuthenticationFilter.class)
                ;
    }
}
