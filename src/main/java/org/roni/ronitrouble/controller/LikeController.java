package org.roni.ronitrouble.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.service.LikeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping // 改变点赞状态
    public void changeLikeStatus(@RequestParam String id, @RequestParam LikeType likeType, @UserId Integer userId) {
        likeService.changeLike(id, likeType, userId);
    }

    @GetMapping
    public Integer getLikeCount(@RequestParam String id, @RequestParam LikeType likeType) {
        return likeService.getLikeCount(id, likeType);
    }

    @GetMapping("/users")
    public List<UserInfo> getPostLikers(@RequestParam("postId") String postId) {
        return likeService.getPostLikers(postId);
    }

    @Operation(summary = "获取点赞状态")
    @GetMapping("/status")
    public Boolean getLikeStatus(@RequestParam String id, @RequestParam LikeType likeType, @UserId Integer userId) {
        if (LikeType.POST_LIKE.equals(likeType)) {
            return likeService.isPostLiked(id, userId);
        } else if (LikeType.COMMENT_LIKE.equals(likeType)) {
            return likeService.isCommentLiked(Integer.valueOf(id), userId);
        }
        return false;
    }

}
