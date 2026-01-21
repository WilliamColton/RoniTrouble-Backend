package org.roni.ronitrouble.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.vie.HomePageResp;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final PostService postService;
    private final UserInfoService userInfoService;
    private final CommentService commentService;
    private final MongoTemplate mongoTemplate;

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @PreDestroy
    public void shutdown() {
        virtualThreadExecutor.shutdown();
    }

    public HomePageResp getHomePage(Integer from, Integer pageSize) {
        Query query = new Query()
                .skip((long) from)
                .limit(pageSize)
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        List<Post> posts = mongoTemplate.find(query, Post.class);

        if (posts.isEmpty()) {
            HomePageResp resp = new HomePageResp();
            resp.setPosts(new ArrayList<>());
            return resp;
        }

        List<Integer> userIds = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<String> postIds = posts.stream()
                .map(Post::getPostId)
                .toList();

        var userInfoFuture = CompletableFuture.supplyAsync(() -> {
            List<UserInfo> userInfos = userInfoService.listByIds(userIds);
            return userInfos.stream()
                    .collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
        }, virtualThreadExecutor);

        var commentFuture = CompletableFuture.supplyAsync(() -> {
            Map<String, List<Comment>> map = new ConcurrentHashMap<>();
            for (String postId : postIds) {
                List<Comment> comments = commentService.getComments(postId);
                map.put(postId, comments);
            }
            return map;
        }, virtualThreadExecutor);

        CompletableFuture.allOf(userInfoFuture, commentFuture).join();

        Map<Integer, UserInfo> userInfoMap = new ConcurrentHashMap<>(userInfoFuture.join());
        Map<String, List<Comment>> commentMap = new ConcurrentHashMap<>(commentFuture.join());

        List<HomePageResp.PostDetail> postDetails = posts.stream()
                .map(post -> {
                    HomePageResp.PostDetail detail = new HomePageResp.PostDetail();
                    detail.setPost(post);
                    detail.setUserInfo(userInfoMap.get(post.getUserId()));
                    detail.setComments(commentMap.getOrDefault(post.getPostId(), new ArrayList<>()));
                    return detail;
                })
                .collect(Collectors.toList());

        HomePageResp resp = new HomePageResp();
        resp.setPosts(postDetails);
        return resp;
    }
}
