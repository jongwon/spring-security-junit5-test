package com.sp.sec.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.service.SpBoardService;
import com.sp.sec.board.service.SpBoardTestHelper;
import com.sp.sec.web.config.JWTUtil;
import com.sp.sec.web.util.RestResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * user1 이 두개의 게시물을 올린다.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RefreshTokenTest extends SpJwtRefreshableTwoUserIntegrationTest{

    @Autowired
    private SpBoardService boardService;

    @Autowired
    private JWTUtil jwtUtil;

    @BeforeEach
    void before() throws URISyntaxException {
        prepareTwoUsers();

        boardService.clearBoards();
        SpBoard board1 = SpBoardTestHelper.makeBoard(USER1, "title1", "content1");
        SpBoard board2 = SpBoardTestHelper.makeBoard(USER1, "title2", "content2");
        Tokens tokens = getToken("user1@test.com", "user1123");
        ResponseEntity<SpBoard> response = restTemplate.exchange(uri("/board/save"),
                HttpMethod.POST, getPostAuthHeaderEntity(tokens.getAccessToken(), board1),
                SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
        response = restTemplate.exchange(uri("/board/save"),
                HttpMethod.POST, getPostAuthHeaderEntity(tokens.getAccessToken(), board2),
                SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @DisplayName("1. user2 가 게시물을 조회하고, 일정 시간이 지나 토큰이 만료된 후 다시 조회한다.")
    @Test
    void test_1() throws URISyntaxException, JsonProcessingException, InterruptedException {
        jwtUtil.getProperties().setTokenLifeTime(1);

        Tokens tokens = getToken("user2@test.com", "user2123");
        ResponseEntity<String> response = restTemplate.exchange(uri("/board/list"),
                HttpMethod.GET, getAuthHeaderEntity(tokens.getAccessToken()), String.class);
        assertEquals(200, response.getStatusCodeValue());
        RestResponsePage<SpBoardSummary> page = objectMapper.readValue(response.getBody(),
                new TypeReference<RestResponsePage<SpBoardSummary>>() {
        });
        assertEquals(2, page.getTotalElements());


        Thread.sleep(2000);

        assertThrows(HttpClientErrorException.class, ()->{
            restTemplate.exchange(uri("/board/list"),
                    HttpMethod.GET, getAuthHeaderEntity(tokens.getAccessToken()), String.class);
        });

        Tokens refreshedTokens = getRefreshToken(tokens.getRefreshToken());
        response = restTemplate.exchange(uri("/board/list"),
                HttpMethod.GET, getAuthHeaderEntity(refreshedTokens.getAccessToken()), String.class);
        assertEquals(200, response.getStatusCodeValue());
        RestResponsePage<SpBoardSummary> page2 = objectMapper.readValue(response.getBody(),
                new TypeReference<RestResponsePage<SpBoardSummary>>() {
                });
        assertEquals(2, page2.getTotalElements());
    }
}
