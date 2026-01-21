package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoService userInfoService;

    //获取个人资料
    @GetMapping
    public UserInfo getUserInfo(@UserId Integer userId) {
        return userInfoService.getUserInfo(userId);
    }

    //更新个人资料
    @PostMapping
    public void addOrUpdateUserInfo(@RequestBody UserInfo userInfo, @UserId Integer userId) {
        userInfoService.addOrUpdateUserInfo(userInfo, userId);
    }
}