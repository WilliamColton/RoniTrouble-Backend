package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comments")
public class Comment {

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;
    private String postId;
    private Integer userId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createAt;
    private String location;

}
