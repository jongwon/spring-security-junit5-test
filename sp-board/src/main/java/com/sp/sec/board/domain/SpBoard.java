package com.sp.sec.board.domain;


import com.sp.sec.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "sp_board")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpBoard {

    @Id
    private String boardId;

    private String title;
    private String content;

    private String writerId;

    @Transient
    private User writer;

    private List<Comment> commentList;

    private LocalDateTime created;
    private LocalDateTime updated;

    private boolean open; // ready, open

}
