package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.comment.req.CreateCommentReq;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.mapper.CommentMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService extends ServiceImpl<CommentMapper, Comment> {

    private final LocationService locationService;
    private final MongoTemplate mongoTemplate;

    public void like(Integer commentId) {
        update(new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getCommentId, commentId)
                .setSql("like_count = like_count+1"));
    }

    public void dislike(Integer commentId) {
        update(new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getCommentId, commentId)
                .gt(Comment::getLikeCount, 0)
                .setSql("like_count = like_count - 1"));
    }

    public void saveComment(CreateCommentReq createCommentReq, Integer userId) {
        Comment comment = new Comment();
        comment.setContent(createCommentReq.getContent());
        comment.setUserId(userId);
        comment.setPostId(createCommentReq.getPostId());
        comment.setCreateAt(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setLocation(locationService.getLocationByUserId(userId));
        save(comment);
    }

    public List<Comment> getComments(String postId) {
        return list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId));
    }

    public void deleteComment(Integer commentId, Integer userId) {
        remove(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getCommentId, commentId)
                .eq(Comment::getUserId, userId));
    }

    public Comment getCommentById(Integer id) {
        return getById(id);
    }


}
