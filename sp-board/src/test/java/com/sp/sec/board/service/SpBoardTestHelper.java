package com.sp.sec.board.service;

import com.sp.sec.board.domain.Comment;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.user.domain.User;
import lombok.AllArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AllArgsConstructor
public class SpBoardTestHelper {

    private final SpBoardService boardService;

    public static SpBoard makeBoard(User user, String title, String content){
        return SpBoard.builder()
                .writerId(user.getUserId())
                .open(true)
                .title(title)
                .content(content)
                .build();
    }

    public SpBoard createBoard(User user, String title, String content){
        return boardService.save(makeBoard(user, title, content));
    }

    public void assertBoard(SpBoard board, User user, String title, String content){
        assertNotNull(board.getBoardId());
        assertNotNull(board.getCreated());
        assertNotNull(board.getUpdated());
        assertEquals(user.getUserId(), board.getWriterId());
        assertEquals(title, board.getTitle());
        assertEquals(content, board.getContent());
    }

    public static Comment makeComment(User user, String commentStr){
        return Comment.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .comment(commentStr)
                .build();
    }

    public Comment createComment(String boardId, User user, String commentStr){
        return boardService.addComment(boardId, makeComment(user, commentStr));
    }

    public void assertComment(Comment comment, User user, String commentStr){
        assertNotNull(comment.getCreated());
        assertNotNull(comment.getCommentId());
        assertEquals(user.getUserId(), comment.getUserId());
        assertEquals(user.getName(), comment.getUserName());
        assertEquals(commentStr, comment.getComment());
    }


    public void assertBoardSummary(SpBoardSummary summary,
                                   SpBoard board, int count){
        assertEquals(board.getBoardId(), summary.getBoardId());
        assertEquals(board.getTitle(), summary.getTitle());
        assertEquals(board.getWriterId(), summary.getWriterId());
        assertEquals(board.getWriter().getName(), summary.getWriter().getName());
        assertEquals(count, summary.getCommentCount());
        assertNotNull(summary.getCreated());
        assertNotNull(summary.getUpdated());
    }
}
