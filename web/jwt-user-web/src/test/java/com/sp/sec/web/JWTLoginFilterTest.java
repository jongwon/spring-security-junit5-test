package com.sp.sec.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.UserTestHelper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import com.sp.sec.web.config.JWTUtil;
import com.sp.sec.web.config.UserLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserTestHelper userTestHelper;

    private RestTemplate restTemplate = new RestTemplate();

    private URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void before(){
        userService.clearUsers();
        this.userTestHelper = new UserTestHelper(userService, passwordEncoder);
        this.userTestHelper.createUser("user1", Authority.ROLE_USER);
    }

    @DisplayName("1. jwt 로 로그인을 시도한다.")
    @Test
    void test_1() throws URISyntaxException {
        UserLogin login = UserLogin.builder().username("user1@test.com")
                .password("user1123").build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @DisplayName("2. 비번이 틀리면 로그인을 하지 못한다.")
    @Test
    void test_2() throws URISyntaxException {
        UserLogin login = UserLogin.builder().username("user1@test.com")
                .password("1234").build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);

        assertThrows(HttpClientErrorException.class, ()->{
            restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);
            // expected 401 에러
        });
    }

}
