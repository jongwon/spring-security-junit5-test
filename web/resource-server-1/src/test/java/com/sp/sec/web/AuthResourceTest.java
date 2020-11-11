package com.sp.sec.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.service.SpBoardService;
import com.sp.sec.board.service.SpBoardTestHelper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.web.config.UserLogin;
import com.sp.sec.web.util.RestResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Auth 서버로 부터
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthResourceTest extends SpJwtRefreshableTwoUserIntegrationTest {

    @Autowired
    private SpBoardService boardService;

    private SpBoardTestHelper boardTestHelper;

    @BeforeEach
    void before(){
        prepareTwoUsers();
        User 인증서버의사용자 = User.builder()
                .userId("5fab670555c23260d5f5fdbf")
                .email("user3@test.com")
                .name("user1")
                .authorities(Set.of(Authority.USER))
                .enabled(true)
                .build();
        userService.save(인증서버의사용자);

        boardTestHelper = new SpBoardTestHelper(boardService);
        boardTestHelper.createBoard(USER1, "title1", "content1");
        boardTestHelper.createBoard(USER1, "title2", "content2");
    }

    @DisplayName("Auth 서버에서 토큰을 받아와서 리소스 서버로 부터 게시판의 게시글을 조회한다.")
    @Test
    void test_1() throws URISyntaxException, JsonProcessingException {
        Tokens 인증서버토큰 = getAuthServerToken("user1@test.com", "1234");
        ResponseEntity<String> response = restTemplate.exchange(uri("/board/list"),
                HttpMethod.GET, getAuthHeaderEntity(인증서버토큰.getAccessToken()), String.class);
        assertEquals(200, response.getStatusCodeValue());

        RestResponsePage<SpBoardSummary> page = objectMapper.readValue(response.getBody(), new TypeReference<RestResponsePage<SpBoardSummary>>() {
        });
        assertEquals(2, page.getTotalElements());
    }

    private Tokens getAuthServerToken(String username, String password) {
        UserLogin login = UserLogin.builder().type(UserLogin.Type.login)
                .username(username).password(password).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(("http://localhost:9001/login"),
                HttpMethod.POST, body, String.class);
        return Tokens.builder()
                .accessToken(getAccessToken(response))
                .refreshToken(getRefreshToken(response))
                .build();
    }
}
