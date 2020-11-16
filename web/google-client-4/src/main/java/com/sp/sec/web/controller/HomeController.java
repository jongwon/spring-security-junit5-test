package com.sp.sec.web.controller;


import com.sp.sec.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private SecuredService securedService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user){
        return securedService.secured();
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public OAuth2User user(@AuthenticationPrincipal OAuth2User user){

        return user;
    }

}
