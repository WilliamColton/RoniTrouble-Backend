package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.dto.post.req.CreateOrUpdatePostReq;
import org.roni.ronitrouble.dto.post.req.SearchPostReq;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.enums.LostAndFoundType;
import org.roni.ronitrouble.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    public Post getPostById(String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/user")
    public List<Post> getPostsByUserId(@UserId Integer userId) {
        return postService.getPostsByUserId(userId);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody CreateOrUpdatePostReq createOrUpdatePostReq, @UserId Integer userId) {
        postService.saveOrUpdatePost(createOrUpdatePostReq, userId);
    }

    @DeleteMapping
    public void delPost(@RequestParam String postId, @UserId Integer userId) {
        postService.delPost(postId, userId);
    }

    @GetMapping("/page")
    public List<Post> getPostsByPage(@RequestParam Integer from, @RequestParam Integer pageSize,
            @RequestParam(required = false) LostAndFoundType lostAndFoundType) {
        return postService.getPostsByPage(from, pageSize, lostAndFoundType);
    }

    @PostMapping("/search")
    public List<Post> searchPosts(@RequestBody SearchPostReq req) {
        return postService.searchPosts(req);
    }

}
