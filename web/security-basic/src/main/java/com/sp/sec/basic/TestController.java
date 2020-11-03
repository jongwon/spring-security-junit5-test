package com.sp.sec.basic;


import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/user")
    public SecurityMessage user() {
        return SecurityMessage.builder()
                .message("user page")
                .auth(SecurityContextHolder.getContext().getAuthentication()).build();
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/admin")
    public SecurityMessage admin() {
        return SecurityMessage.builder()
                .message("admin page")
                .auth(SecurityContextHolder.getContext().getAuthentication()).build();
    }

}
