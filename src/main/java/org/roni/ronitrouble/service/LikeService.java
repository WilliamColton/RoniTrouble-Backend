package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Like;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.mapper.LikeMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LikeService extends ServiceImpl<LikeMapper, Like> {
    private final PostService postService;
    private final CommentService commentService;

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

}
