package com.sp.sec.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @RequestMapping("/")
    @PreAuthorize("isAuthenticated()")
    public OAuth2User home(@AuthenticationPrincipal OAuth2User user){
        return user;
    }

    @RequestMapping("/site")
    @PreAuthorize("isAuthenticated()")
    public UserDetails siteUser(@AuthenticationPrincipal UserDetails user){
        return user;
    }

}
