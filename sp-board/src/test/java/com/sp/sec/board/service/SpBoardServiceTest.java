package com.sp.sec.board.service;

import com.sp.sec.board.domain.Comment;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.repository.SpBoardRepository;
import com.sp.sec.user.WithUserTest;
import com.sp.sec.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"board-test"})
@DataMongoTest
class SpBoardServiceTest extends WithUserTest {

    @Autowired
    private SpBoardRepository boardRepository;

    private SpBoardService boardService;
    private SpBoardTestHelper boardTestHelper;
    private User user1;

    @BeforeEach
    void before(){
        prepareUserService();
        this.boardService = new SpBoardService(mongoTemplate, boardRepository, userService);
        boardService.clearBoards();
        this.boardTestHelper = new SpBoardTestHelper(boardService);
        this.user1 = userTestHelper.createUser("user1");
    }

    @DisplayName("1. 게시판에 글을 올린다.")
    @Test
    void test_1() {
        boardTestHelper.createBoard(user1, "title1", "content1");
        List<SpBoard> boardList = boardRepository.findAll();
        assertEquals(1, boardList.size());
        boardTestHelper.assertBoard(boardList.get(0), user1, "title1", "content1");
    }


    @DisplayName("2. 게시판의 내용을 수정한다.")
    @Test
    void test_2() {
        SpBoard board = boardTestHelper.createBoard(user1, "title1", "content1");
        boardService.updateContent(board.getBoardId(), "content2");
        assertEquals("content2", boardService.findBoard(board.getBoardId()).get().getContent());
        boardService.updateTitle(board.getBoardId(), "title2");
        assertEquals("title2", boardService.findBoard(board.getBoardId()).get().getTitle());
        boardService.update(board.getBoardId(), "title3", "content3");
        assertEquals("title3", boardService.findBoard(board.getBoardId()).get().getTitle());
        assertEquals("content3", boardService.findBoard(board.getBoardId()).get().getContent());
    }


    @DisplayName("2.1 게시물을 삭제한다.")
    @Test
    void test_2_1() {
        SpBoard board = boardTestHelper.createBoard(user1, "title1", "content1");
        boardService.removeBoard(board.getBoardId());
        assertTrue(boardService.findBoard(board.getBoardId()).isEmpty());
    }

    @DisplayName("3. 게시판에 코멘트를 남긴다.")
    @Test
    void test_3() {
        SpBoard board = boardTestHelper.createBoard(user1, "title1", "content1");
        boardTestHelper.createComment(board.getBoardId(), user1, "comment1");

        SpBoard saved = boardService.findBoard(board.getBoardId()).get();
        assertEquals(1, saved.getCommentList().size());
        boardTestHelper.assertComment(saved.getCommentList().get(0), user1, "comment1" );
    }

    @DisplayName("4. 게시판에 코멘트를 삭제한다.")
    @Test
    void test_4() {
        SpBoard board = boardTestHelper.createBoard(user1, "title1", "content1");
        Comment comment = boardTestHelper.createComment(board.getBoardId(), user1, "comment1");
        boardService.removeComment(board.getBoardId(), comment.getCommentId());
        SpBoard saved = boardService.findBoard(board.getBoardId()).get();
        assertEquals(0, saved.getCommentList().size());
    }

    @DisplayName("4.1 코멘트가 3개일 때 가운데 코멘트를 지운다.")
    @Test
    void test_4_1() {
        SpBoard board = boardTestHelper.createBoard(user1, "title1", "content1");
        Comment comment1 = boardTestHelper.createComment(board.getBoardId(), user1, "comment1");
        Comment comment2 = boardTestHelper.createComment(board.getBoardId(), user1, "comment2");
        Comment comment3 = boardTestHelper.createComment(board.getBoardId(), user1, "comment3");

        boardService.removeComment(board.getBoardId(), comment2.getCommentId());
        SpBoard saved = boardService.findBoard(board.getBoardId()).get();
        assertEquals(2, saved.getCommentList().size());
        boardTestHelper.assertComment(saved.getCommentList().get(0), user1, "comment1" );
        boardTestHelper.assertComment(saved.getCommentList().get(1), user1, "comment3" );
    }

    @DisplayName("5. board summary list 를 가져온다.")
    @Test
    void test_5() {
        SpBoard board1 = boardTestHelper.createBoard(user1, "title1", "content1");
        SpBoard board2 = boardTestHelper.createBoard(user1, "title2", "content2");
        boardTestHelper.createComment(board1.getBoardId(), user1, "comment1");
        boardTestHelper.createComment(board2.getBoardId(), user1, "comment1");
        boardTestHelper.createComment(board2.getBoardId(), user1, "comment2");
        boardTestHelper.createComment(board2.getBoardId(), user1, "comment3");

        Page<SpBoardSummary> list = boardService.list(1, 10);
        assertEquals(2, list.getContent().size());
        assertEquals(1, list.getTotalPages());
        assertEquals(2, list.getTotalElements());

        board1.setWriter(user1);
        board2.setWriter(user1);
        boardTestHelper.assertBoardSummary(list.getContent().get(1), board1, 1);
        boardTestHelper.assertBoardSummary(list.getContent().get(0), board2, 3);
    }


}