package org.roni.ronitrouble.controller;


import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.dto.comment.req.CreateCommentReq;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping//上传评论
    public void saveComment(@RequestBody CreateCommentReq createCommentReq, @UserId Integer userId) {
        commentService.saveComment(createCommentReq, userId);
    }

    @GetMapping//获取评论
    public List<Comment> getComments(@RequestParam String postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping//删除评论
    public void deleteComment(@RequestParam Integer commentId, @UserId Integer userId) {
        commentService.deleteComment(commentId, userId);
    }
}
