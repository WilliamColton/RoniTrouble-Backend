package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.roni.ronitrouble.enums.LikeType;

@Data
@TableName("likes")
public class Like {
    @TableId(type = IdType.AUTO)
    private Integer likeId;
    private Integer commentId;
    private String postId;
    private Integer userId;
    private LikeType likeType;
}
