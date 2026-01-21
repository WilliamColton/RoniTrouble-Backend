package org.roni.ronitrouble.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private Integer commentId;
    private String postId;
    private Integer userId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createAt;
    private String location;

}
