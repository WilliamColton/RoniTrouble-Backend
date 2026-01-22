package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.entity.Like;
import org.roni.ronitrouble.entity.NotificationHistory;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.enums.NotificationType;
import org.roni.ronitrouble.mapper.LikeMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LikeService extends ServiceImpl<LikeMapper, Like> {
    private final PostService postService;
    private final CommentService commentService;
    private final UserInfoService userInfoService;
    private final NotificationHistoryService notificationHistoryService;

    public LikeService(PostService postService, CommentService commentService,
            @Lazy UserInfoService userInfoService,
            NotificationHistoryService notificationHistoryService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userInfoService = userInfoService;
        this.notificationHistoryService = notificationHistoryService;
    }

    public void changeLike(String id, LikeType likeType, Integer userId) {
        if (LikeType.COMMENT_LIKE.equals(likeType)) {
            try {
                changeCommentLikeStatus(Integer.valueOf(id), userId);
            } catch (NumberFormatException e) {
                throw new RuntimeException("评论ID必须是数字格式");
            }
        } else if (LikeType.POST_LIKE.equals(likeType)) {
            changePostLikeStatus(id, userId);
        }
    }

    public void changeCommentLikeStatus(Integer commentId, Integer userId) {
        var query = new LambdaQueryWrapper<Like>()
                .eq(Like::getCommentId, commentId)
                .eq(Like::getUserId, userId);
        Like like;
        like = getOne(query);
        if (like == null) {
            like = new Like();
            like.setUserId(userId);
            like.setCommentId(commentId);
            like.setLikeType(LikeType.COMMENT_LIKE);
            save(like);
            commentService.like(commentId);

            Comment comment = commentService.getCommentById(commentId);
            if (comment != null && !comment.getUserId().equals(userId)) {
                NotificationHistory notification = NotificationHistory.builder()
                        .userId(comment.getUserId())
                        .opponentId(userId)
                        .notificationType(NotificationType.Like)
                        .postId(comment.getPostId())
                        .commentId(commentId)
                        .createAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationHistoryService.saveNotification(notification);
            }
        } else {
            remove(query);
            commentService.dislike(commentId);
        }

    }

    public void changePostLikeStatus(String postId, Integer userId) {
        var just = new LambdaQueryWrapper<Like>()
                .eq(Like::getPostId, postId)
                .eq(Like::getUserId, userId);

        Like like;
        like = getOne(just);
        if (like == null) {
            like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setLikeType(LikeType.POST_LIKE);
            save(like);
            postService.like(postId);

            Post post = postService.getPostById(postId);
            if (post != null && !post.getUserId().equals(userId)) {
                NotificationHistory notification = NotificationHistory.builder()
                        .userId(post.getUserId())
                        .opponentId(userId)
                        .notificationType(NotificationType.Like)
                        .postId(postId)
                        .createAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationHistoryService.saveNotification(notification);
            }
        } else {
            remove(just);
            postService.dislike(postId);
        }
    }

    public Integer getLikeCount(String id, LikeType likeType) {
        if (LikeType.POST_LIKE.equals(likeType)) {
            return postService.getPostById(id).getLikeCount();
        } else {
            return commentService.getCommentById(Integer.valueOf(id)).getLikeCount();
        }
    }

    public List<org.roni.ronitrouble.entity.UserInfo> getPostLikers(String postId) {
        var query = new LambdaQueryWrapper<Like>()
                .eq(Like::getPostId, postId)
                .eq(Like::getLikeType, LikeType.POST_LIKE);
        List<Like> likes = list(query);
        if (likes == null || likes.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> userIds = likes.stream()
                .map(Like::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userInfoService.listByIds(userIds);
    }

    public boolean isPostLiked(String postId, Integer userId) {
        return count(new LambdaQueryWrapper<Like>()
                .eq(Like::getPostId, postId)
                .eq(Like::getUserId, userId)
                .eq(Like::getLikeType, LikeType.POST_LIKE)) > 0;
    }

    public boolean isCommentLiked(Integer commentId, Integer userId) {
        return count(new LambdaQueryWrapper<Like>()
                .eq(Like::getCommentId, commentId)
                .eq(Like::getUserId, userId)
                .eq(Like::getLikeType, LikeType.COMMENT_LIKE)) > 0;
    }

}
