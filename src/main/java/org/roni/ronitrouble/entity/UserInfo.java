package org.roni.ronitrouble.entity;

import lombok.Data;

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
    private Integer viewCount;
    private Integer likesFavoritesCount;
}
