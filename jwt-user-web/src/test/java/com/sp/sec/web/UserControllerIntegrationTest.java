package com.sp.sec.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import com.sp.sec.web.config.JWTUtil;
import com.sp.sec.web.config.UserLogin;
import com.sp.sec.web.controller.RestResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {


    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private RestTemplate restTemplate = new RestTemplate();

    private User USER1;
    private User ADMIN;

    private URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void before(){
        userService.clearUsers();
        User user1 = User.builder()
                .email("user1@test.com")
                .name("user1")
                .password(passwordEncoder.encode("1234"))
                .authorities(Set.of(Authority.USER))
                .enabled(true)
                .build();
        User admin = User.builder()
                .email("admin@test.com")
                .name("admin")
                .password(passwordEncoder.encode("admin"))
                .authorities(Set.of(Authority.ADMIN))
                .enabled(true)
                .build();

        this.USER1 = userService.save(user1);
        this.ADMIN = userService.save(admin);
    }

    private String getToken(String username, String password) throws URISyntaxException {
        UserLogin login = UserLogin.builder().username(username).password(password).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"),
                HttpMethod.POST, body, String.class);
        assertEquals(200, response.getStatusCodeValue());
        return response.getHeaders().get(JWTUtil.AUTH_HEADER).get(0)
                .substring(JWTUtil.BEARER.length());
    }

    private HttpEntity getAuthHeaderEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTH_HEADER, JWTUtil.BEARER+ accessToken);
        HttpEntity entity = new HttpEntity("", headers);
        return entity;
    }


    @DisplayName("1. admin 유저는 userList 를 가져올 수 있다.")
    @Test
    void test_1() throws URISyntaxException, JsonProcessingException {
        String accessToken = getToken("admin@test.com", "admin");

        ResponseEntity<String> response = restTemplate.exchange(uri("/user/list"),
                HttpMethod.GET, getAuthHeaderEntity(accessToken), String.class);

        RestResponsePage<User> page = objectMapper.readValue(response.getBody(),
                new TypeReference<RestResponsePage<User>>() {
                });

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().map(user->user.getName())
        .collect(Collectors.toSet()).containsAll(Set.of("user1", "admin")));

        page.getContent().forEach(System.out::println);
    }


    @DisplayName("2. user1 에게 admin 권한을 준다.")
    @Test
    void test_2() throws URISyntaxException, JsonProcessingException {

        // token
        String accessToken = getToken("admin@test.com", "admin");

        // user1 에게 admin 권한을 준다.
        ResponseEntity<String> response = restTemplate.exchange(uri(
                format("/user/authority/add?userId=%s&authority=%s", USER1.getUserId(), Authority.ROLE_ADMIN)),
                HttpMethod.PUT, getAuthHeaderEntity(accessToken), String.class);

        assertEquals(200, response.getStatusCodeValue());

        // user1 데이터를 가져와서 확인한다.
        ResponseEntity<String> response2 = restTemplate.exchange(uri(
                format("/user/%s", USER1.getUserId())),
                HttpMethod.GET, getAuthHeaderEntity(accessToken), String.class);
        assertEquals(200, response2.getStatusCodeValue());

        User respUser = objectMapper.readValue(response2.getBody(), User.class);
        assertTrue(respUser.getAuthorities().contains(Authority.ADMIN));

    }

}
