package org.roni.ronitrouble.service;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.post.req.CreateOrUpdatePostReq;
import org.roni.ronitrouble.entity.Like;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.mapper.LikeMapper;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final LocationService locationService;
    private final MongoTemplate mongoTemplate;
    private final LikeMapper likeMapper;

    public void like(String postId) {
        mongoTemplate.updateFirst(Query.query(Criteria
                .where("postId")
                .is(postId)),
                new Update().inc("likeCount", 1), Post.class);
    }

    public List<Post> getPostsByUserId(Integer userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Post.class);
    }

    public Boolean isPostExisted(String postId) {
        return mongoTemplate.findOne(Query.query(Criteria
                .where("postId")
                .is(postId)), Post.class) != null;
    }

    public void saveOrUpdatePost(CreateOrUpdatePostReq createOrUpdatePostReq, Integer userId) {
        if (createOrUpdatePostReq.getPostId() == null || createOrUpdatePostReq.getPostId().isEmpty()) {
            savePost(createOrUpdatePostReq, userId);
        } else {
            updatePost(createOrUpdatePostReq);
        }
    }

    private void savePost(CreateOrUpdatePostReq createOrUpdatePostReq, Integer userId) {
        var post = MapperUtil.mapper(Post.class, createOrUpdatePostReq);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(userId);
        post.setViewCount(0);
        post.setLocation(locationService.getLocationByUserId(userId));
        post.setImageUrls(createOrUpdatePostReq.getImageUrls());
        mongoTemplate.save(post);
    }

    private void updatePost(CreateOrUpdatePostReq createOrUpdatePostReq) {
        Query query = Query.query(Criteria.where("postId").is(createOrUpdatePostReq.getPostId()));
        Update update = new Update()
                .set("content", createOrUpdatePostReq.getContent())
                .set("postType", createOrUpdatePostReq.getPostType())
                .set("imageUrls", createOrUpdatePostReq.getImageUrls());
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public void delPost(String postId, Integer userId) {
        mongoTemplate.remove(Query.query(Criteria
                .where("postId").is(postId)
                .and("userId").is(userId)), Post.class);
    }

    public List<Post> getPostsByPage(Integer from, Integer pageSize) {
        Query query = new Query().skip((long) from).limit(pageSize);
        return mongoTemplate.find(query, Post.class);
    }

    public List<Post> searchPosts(org.roni.ronitrouble.dto.post.req.SearchPostReq req) {
        Query query = new Query();

        if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
            query.addCriteria(Criteria.where("content").regex(req.getKeyword(), "i"));
        }
        if (req.getPostType() != null) {
            query.addCriteria(Criteria.where("postType").is(req.getPostType()));
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Post.class);
    }

    public Post getPostById(String postId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("postId").is(postId)),
                new Update().inc("viewCount", 1),
                Post.class
        );
        return mongoTemplate.findById(postId, Post.class);
    }

    public void dislike(String postId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("postId").is(postId).and("likeCount").gt(0)),
                new Update().inc("likeCount", -1),
                Post.class);

    }

    public List<Post> getRecentPostsByUserId(Integer userId, Integer n) {
        Query query = Query.query(Criteria.where("userId").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .limit(n);
        return mongoTemplate.find(query, Post.class);
    }

    public List<Post> getRecentLikedPosts(Integer userId, Integer n) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("likeType", LikeType.POST_LIKE)
                .orderByDesc("likeId")
                .last("LIMIT " + n);

        List<Like> likes = likeMapper.selectList(queryWrapper);
        List<String> postIds = likes.stream()
                .map(Like::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return List.of();
        }

        Query query = Query.query(Criteria.where("postId").in(postIds))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Post.class);
    }

}
