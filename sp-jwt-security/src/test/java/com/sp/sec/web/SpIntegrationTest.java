package com.sp.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.config.JWTUtil;
import com.sp.sec.web.config.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

public class SpIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected ObjectMapper objectMapper;

    protected RestTemplate restTemplate = new RestTemplate();


    protected URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    protected URI uri(String path, String... args) throws URISyntaxException {
        return uri(format(path, args));
    }

    protected Tokens getToken(String username, String password) throws URISyntaxException {
        UserLogin login = UserLogin.builder().type(UserLogin.Type.login).username(username).password(password).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"),
                HttpMethod.POST, body, String.class);
        return Tokens.builder().accessToken(getAccessToken(response)).build();
    }

    protected String getAccessToken(ResponseEntity<String> response) {
        return response.getHeaders().get(JWTUtil.AUTH_HEADER).get(0)
                .substring(JWTUtil.BEARER.length());
    }

    protected HttpEntity getAuthHeaderEntity(String accessToken) {
        return getPostAuthHeaderEntity(accessToken, null);
    }

    protected HttpEntity getPostAuthHeaderEntity(String accessToken, Object object) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTH_HEADER, JWTUtil.BEARER+ accessToken);
        HttpEntity entity = new HttpEntity(object, headers);
        return entity;
    }

}
