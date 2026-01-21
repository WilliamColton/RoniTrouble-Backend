package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfo {
    @TableId(value = "user_id", type = IdType.INPUT)
    private Integer userId;
    private String nickname;
    private String avtarUrl;
    private String introduction;
    private LocalDate createAt;
    private LocalDate birthday;
    private String location;
    private Integer postCount;
    private Integer viewCount;
    private Integer likesFavoritesCount;
}
