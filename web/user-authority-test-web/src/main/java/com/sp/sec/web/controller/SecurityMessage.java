package com.sp.sec.web.controller;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityMessage {

    private String message;

    @JsonIgnore
    private Authentication auth;

}
