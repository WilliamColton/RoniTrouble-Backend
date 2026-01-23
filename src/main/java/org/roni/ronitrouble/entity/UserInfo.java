package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.roni.ronitrouble.enums.Gender;

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
    private Gender gender;
    private Integer postCount;
    private Integer beLikedCount;
    private Integer likesFavoritesCount;

}
