package org.roni.ronitrouble.entity;

import lombok.Data;
import org.roni.ronitrouble.enums.Gender;

import java.time.LocalDate;

@Data
public class UserInfo {
    private Integer userId;
    private String nickname;
    private String avtarUrl;
    private String introduction;
    private LocalDate createAt;
    private LocalDate birthday;
    private String location;
    private Integer postCount;
    private Gender gender;
    private Integer viewCount;
    private Integer likesFavoritesCount;
}
