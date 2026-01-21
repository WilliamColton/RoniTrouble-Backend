package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.service.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping//改变点赞状态
    public void changeLikeStatus(@RequestParam String id, @RequestParam LikeType likeType, @UserId Integer userId) {
        likeService.changeLike(id, likeType, userId);
    }

    @GetMapping
    public Integer getLikeCount(@RequestParam String id, @RequestParam LikeType likeType) {
        return likeService.getLikeCount(id, likeType);
    }

}
