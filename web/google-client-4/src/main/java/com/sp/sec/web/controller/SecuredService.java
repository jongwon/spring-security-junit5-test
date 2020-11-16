package com.sp.sec.web.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SecuredService {

    @PreAuthorize("hasAnyAuthority('FROM_GOOGLE')")
    public String secured(){
        return "secured info : 옥수수";
    }

}
