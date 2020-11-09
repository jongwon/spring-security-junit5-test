package com.sp.sec.board.controller;

import com.sp.sec.board.domain.Comment;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.service.SpBoardService;
import com.sp.sec.user.domain.User;
import com.sp.sec.web.util.RestResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final SpBoardService boardService;

    @PreAuthorize("isAuthenticated() and (#board.boardId == null or #board.writerId == #user.userId)")
    @PostMapping("/save")
    public SpBoard save(
            @RequestBody SpBoard board,
            @AuthenticationPrincipal User user
    ){
        if(StringUtils.isEmpty(board.getBoardId())){
            board.setWriterId(user.getUserId());
        }
        return boardService.save(board);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public RestResponsePage<SpBoardSummary> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer size
    ){
        return RestResponsePage.of(boardService.list(pageNum, size));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardId}")
    public Optional<SpBoard> getBoard(@PathVariable String boardId){
        return boardService.findBoard(boardId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{boardId}")
    public Optional<SpBoard> remove(
            @PathVariable String boardId,
            @AuthenticationPrincipal User user
    ){
        return boardService.findBoard(boardId).map(board->{
            if(board.getWriterId().equals(user.getUserId())){
                boardService.removeBoard(boardId);
            }else{
                throw new AccessDeniedException("게시자만 삭제할 수 있습니다.");
            }
            return board;
        });
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{boardId}/comment")
    public Comment addComment(
            @PathVariable String boardId,
            @RequestBody String comment,
            @AuthenticationPrincipal User user
    ){
        return boardService.addComment(boardId, Comment.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .comment(comment)
                .build());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{boardId}/comment/{commentId}")
    public Optional<Boolean> removeComment(
            @PathVariable String boardId,
            @PathVariable String commentId,
            @AuthenticationPrincipal User user
    ){
        return boardService.findBoard(boardId).map(board->{
            if(board.getCommentList() == null) return false;
            Optional<Comment> comment = board.getCommentList().stream().filter(c->c.getCommentId().equals(commentId)).findFirst();
            if(comment.isPresent()){
                if(comment.get().getUserId().equals(user.getUserId())){
                    boardService.removeComment(boardId, commentId);
                    return true;
                }else{
                    throw new AccessDeniedException("댓글을 생성한 사람만 삭제할 수 있습니다.");
                }
            }
            return false;
        });
    }

}
