package org.roni.ronitrouble.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userInfo")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoService userInfoService;

    @Operation(summary = "通过用户 id 查找信息")
    @GetMapping
    public UserInfo getUserInfoById(@RequestParam Integer userId) {
        return userInfoService.getUserInfo(userId);
    }

    @Operation(summary = "获取当前登录用户的信息")
    @GetMapping("/me")
    public UserInfo getMyUserInfo(@UserId Integer userId) {
        return userInfoService.getUserInfo(userId);
    }

    @GetMapping("/isExisted")
    public Boolean isUserInfoByIdExisted(@UserId Integer userId) {
        return userInfoService.isUserInfoByIdExisted(userId);
    }

    @Operation(description = "判断传入的用户 id 是否等于当前登录的用户 id")
    @GetMapping("/isCurrentUser")
    public Boolean isCurrentUser(@RequestParam Integer userId, @UserId Integer currentUserId) {
        return userId.equals(currentUserId);
    }

    @PostMapping
    public void addOrUpdateUserInfo(@RequestBody UserInfo userInfo, @UserId Integer userId) {
        userInfoService.addOrUpdateUserInfo(userInfo, userId);
    }

    @GetMapping("/posts")
    public List<Post> getUserPosts(@UserId Integer userId) {
        return userInfoService.getUserPosts(userId);
    }

    @GetMapping("/comments")
    public List<Comment> getUserComments(@UserId Integer userId) {
        return userInfoService.getUserComments(userId);
    }

    @GetMapping("/likedPosts")
    public List<Post> getUserLikedPosts(@UserId Integer userId) {
        return userInfoService.getUserLikedPosts(userId);
    }

    @GetMapping("/postCount")
    public Integer getUserPostCount(@UserId Integer userId) {
        return userInfoService.getUserPostCount(userId);
    }

    @GetMapping("/viewCount")
    public Integer getUserViewCount(@UserId Integer userId) {
        return userInfoService.getUserViewCount(userId);
    }

    @GetMapping("/likeCount")
    public Integer getUserLikeCount(@UserId Integer userId) {
        return userInfoService.getUserLikeCount(userId);
    }

}
