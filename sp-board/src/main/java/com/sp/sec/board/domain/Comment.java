package com.sp.sec.board.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    private String commentId;

    private String comment;
    private String userId;
    private String userName;

    private LocalDateTime created;

}
