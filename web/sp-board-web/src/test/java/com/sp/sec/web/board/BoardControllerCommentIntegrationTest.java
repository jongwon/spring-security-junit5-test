package com.sp.sec.web.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sp.sec.board.domain.Comment;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * user1 이 게시글을 올리고
 * user2 가 댓글을 단다.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardControllerCommentIntegrationTest extends SpJwtTwoUserIntegrationTest {

    private SpBoard board;
    private Comment comment;

    @Autowired
    private SpBoardService boardService;


    @BeforeEach
    void before() throws URISyntaxException {
        prepareTwoUsers();

        boardService.clearBoards();
        SpBoard writtenBoard = SpBoardTestHelper.makeBoard(USER1, "title", "content");
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<SpBoard> response = restTemplate.exchange(uri("/board/save"), HttpMethod.POST, getPostAuthHeaderEntity(user1Token, writtenBoard),
                SpBoard.class);
        assertEquals(200, response.getStatusCodeValue());
        this.board = response.getBody();
        this.board.setWriter(USER1);
        String user2Token = getToken("user2@test.com", "user2123");
        ResponseEntity<Comment> response2 = restTemplate.exchange(uri("/board/%s/comment", this.board.getBoardId()), HttpMethod.PUT,
                getPostAuthHeaderEntity(user2Token, "comment1"), Comment.class);
        assertEquals(200, response2.getStatusCodeValue());
        this.comment = response2.getBody();
    }

    //
    @DisplayName("1. user2가 게시판에 댓글을 단다.")
    @Test
    void test_1() throws URISyntaxException {
        String user2Token = getToken("user2@test.com", "user2123");
        ResponseEntity<SpBoard> resp = restTemplate.exchange(uri("/board/%s", this.board.getBoardId()), HttpMethod.GET, getAuthHeaderEntity(user2Token), SpBoard.class);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody().getCommentList());
        assertEquals(1, resp.getBody().getCommentList().size());
    }

    @DisplayName("2. user2가 자신이 단 댓글을 삭제한다.")
    @Test
    void test_2() throws URISyntaxException {
        String user2Token = getToken("user2@test.com", "user2123");
        ResponseEntity<Boolean> resp = restTemplate.exchange(uri("/board/%s/comment/%s",
                this.board.getBoardId(), this.comment.getCommentId()),
                HttpMethod.DELETE, getAuthHeaderEntity(user2Token), Boolean.class);
        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody());
        ResponseEntity<SpBoard> resp2 = restTemplate.exchange(uri("/board/%s", this.board.getBoardId()), HttpMethod.GET, getAuthHeaderEntity(user2Token), SpBoard.class);
        assertEquals(200, resp2.getStatusCodeValue());
        assertEquals(0, resp2.getBody().getCommentList().size());
    }


    @DisplayName("2-1. user2가 단 댓글을 user1이 삭제하지 못한다.")
    @Test
    void test_2_1() throws URISyntaxException {
        String user1Token = getToken("user1@test.com", "user1123");
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, ()->{
            restTemplate.exchange(uri("/board/%s/comment/%s",
                this.board.getBoardId(), this.comment.getCommentId()),
                HttpMethod.DELETE, getAuthHeaderEntity(user1Token), Boolean.class);
        });
        assertEquals(403, exception.getRawStatusCode());
    }

    @DisplayName("3. 댓글 리스트를 summary 리스트로 내려 봤을 때 댓글의 개수가 그대로 반영된다.")
    @Test
    void test_3() throws URISyntaxException, JsonProcessingException {
        String user1Token = getToken("user1@test.com", "user1123");
        ResponseEntity<String> response = restTemplate.exchange(uri("/board/list"), HttpMethod.GET, getAuthHeaderEntity(user1Token), String.class);
        assertEquals(200, response.getStatusCodeValue());
        RestResponsePage<SpBoardSummary> page = objectMapper.readValue(response.getBody(), new TypeReference<RestResponsePage<SpBoardSummary>>() {
        });
        assertEquals(1, page.getTotalElements());
        SpBoardTestHelper.assertBoardSummary(page.getContent().get(0), board, 1);
    }

}
