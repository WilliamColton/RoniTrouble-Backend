package org.roni.ronitrouble.entity;

import lombok.Data;
import org.roni.ronitrouble.enums.PostType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> imageUrls;
    private LocalDateTime createdAt;

}
