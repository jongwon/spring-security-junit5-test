package com.sp.sec.web.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.service.SpBoardService;
import com.sp.sec.board.service.SpBoardTestHelper;
import com.sp.sec.web.SpJwtTwoUserIntegrationTest;
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
 * when : user1 이 게시글을 올린다.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardControllerIntegrationTest extends SpJwtTwoUserIntegrationTest {

    private SpBoard board;
    @Autowired
    private SpBoardService boardService;

    @BeforeEach
    void before() throws URISyntaxException {
        prepareTwoUsers();

        boardService.clearBoards();
        SpBoard board = SpBoardTestHelper.makeBoard(USER1, "title", "content");
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<SpBoard> response = restTemplate.exchange(uri("/board/save"), HttpMethod.POST, getPostAuthHeaderEntity(user1Token, board),
                SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
        this.board = response.getBody();
        this.board.setWriter(USER1);
    }


    //
    @DisplayName("1. user1이 게시글을 조회한다.")
    @Test
    void test_1() throws URISyntaxException, JsonProcessingException {
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<String> response = restTemplate.exchange(uri("/board/list"), HttpMethod.GET, getAuthHeaderEntity(user1Token), String.class);
        assertEquals(200, response.getStatusCodeValue());
        RestResponsePage<SpBoardSummary> page = objectMapper.readValue(response.getBody(), new TypeReference<RestResponsePage<SpBoardSummary>>() {
        });
        assertEquals(1, page.getTotalElements());
        SpBoardTestHelper.assertBoardSummary(page.getContent().get(0), board, 0);
    }

    //
    @DisplayName("2. user1이 자신의 게시글을 삭제한다.")
    @Test
    void test_2() throws URISyntaxException {
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<SpBoard> response = restTemplate.exchange(uri("/board/%s", board.getBoardId()),
                HttpMethod.DELETE, getAuthHeaderEntity(user1Token), SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
        SpBoardTestHelper.assertBoard(response.getBody(), USER1, "title", "content");
    }

    //
    @DisplayName("2-1. user1이 올린 게시글은 user2가 삭제하지 못한다.")
    @Test
    void test_2_1() throws URISyntaxException {
        String user2Token = getToken("user2@test.com", "user2123");
        HttpClientErrorException excpetion = assertThrows(HttpClientErrorException.class, ()->{
            restTemplate.exchange(uri("/board/%s", board.getBoardId()),
                    HttpMethod.DELETE, getAuthHeaderEntity(user2Token), SpBoard.class);
        });
        assertEquals(403, excpetion.getRawStatusCode());
    }

    //
    @DisplayName("3. user1이 올린 게시물을 수정한다.")
    @Test
    void test_3() throws URISyntaxException {
        this.board.setTitle("title2");
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<SpBoard> response = restTemplate.exchange(uri("/board/save"),
                HttpMethod.POST, getPostAuthHeaderEntity(user1Token, board),
                SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
        SpBoardTestHelper.assertBoard(response.getBody(), USER1, "title2", "content");
    }

    //
    @DisplayName("3-1. user1이 올린 게시물을 user2가 수정하지 못한다.")
    @Test
    void test_3_1() throws URISyntaxException {
        this.board.setTitle("title2");
        final String user2Token = getToken("user2@test.com", "user2123");
        HttpClientErrorException excpetion = assertThrows(HttpClientErrorException.class, ()->{
            restTemplate.exchange(uri("/board/save"),
                    HttpMethod.POST, getPostAuthHeaderEntity(user2Token, board),
                    SpBoard.class);
        });
        assertEquals(403, excpetion.getRawStatusCode());
    }

}
