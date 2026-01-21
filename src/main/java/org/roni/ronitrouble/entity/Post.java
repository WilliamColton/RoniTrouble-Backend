package org.roni.ronitrouble.entity;

import lombok.Data;
import org.roni.ronitrouble.enums.LostAndFoundType;
import org.roni.ronitrouble.enums.PostType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Post {

    @Id
    private String postId;
    private Integer userId;
    private String content;
    private String location;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private PostType postType;
    private LocalDateTime createdAt;

    private Double score;
    private Integer cuisineId;
    private Integer merchantId;

    private LostAndFoundType lostAndFoundType;

}
