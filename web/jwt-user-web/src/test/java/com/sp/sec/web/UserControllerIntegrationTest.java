package com.sp.sec.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.UserTestHelper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import com.sp.sec.web.config.JWTUtil;
import com.sp.sec.web.config.SpJwtProperties;
import com.sp.sec.web.config.UserLogin;
import com.sp.sec.web.util.RestResponsePage;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest extends SpJwtUserAdminIntegrationTest {

    @BeforeEach
    void before(){
        prepareUserAdmin();
    }

    @DisplayName("1-1. user1 은 자신의 정보를 조회할 수 있다.")
    @Test
    void test_1_1() throws URISyntaxException {
        String accessToken = getToken("user1@test.com", "user1123");
        ResponseEntity<User> response = restTemplate.exchange(uri("/user/"+USER1.getUserId()),
                HttpMethod.GET, getAuthHeaderEntity(accessToken), User.class);

        assertEquals(200, response.getStatusCodeValue());
        userTestHelper.assertUser(response.getBody(), "user1");
    }

    @DisplayName("1. admin 유저는 userList 를 가져올 수 있다.")
    @Test
    void test_1() throws URISyntaxException, JsonProcessingException {
        String accessToken = getToken("admin@test.com", "admin123");

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
        String accessToken = getToken("admin@test.com", "admin123");

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
