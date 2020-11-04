package com.sp.sec.board.domain;


import com.sp.sec.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpBoardSummary {

    private String boardId;
    private String title;
    private String writerId;

    @Transient
    private User writer;

    private int commentCount;

    private LocalDateTime created;
    private LocalDateTime updated;

    public static SpBoardSummary of(SpBoard board, User writer){
        return SpBoardSummary.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .writerId(board.getWriterId())
                .writer(writer)
                .commentCount(board.getCommentList() == null ? 0 : board.getCommentList().size())
                .created(board.getCreated())
                .updated(board.getUpdated())
                .build();
    }

}
