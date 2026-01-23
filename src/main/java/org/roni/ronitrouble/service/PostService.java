package org.roni.ronitrouble.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.component.cache.impl.PostCache;
import org.roni.ronitrouble.component.store.vectorStore.impl.PostStore;
import org.roni.ronitrouble.dto.post.req.CreateOrUpdatePostReq;
import org.roni.ronitrouble.dto.post.resp.PostWithAuthorInfo;
import org.roni.ronitrouble.entity.Like;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.enums.PostType;
import org.roni.ronitrouble.mapper.LikeMapper;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    private final LocationService locationService;
    private final MongoTemplate mongoTemplate;
    private final LikeMapper likeMapper;
    private final PostCache postCache;
    private final EmbeddingService embeddingService;
    private final PostStore postStore;
    private final UserInfoService userInfoService;

    public PostService(LocationService locationService, MongoTemplate mongoTemplate,
                       LikeMapper likeMapper, PostCache postCache,
                       EmbeddingService embeddingService, PostStore postStore,
                       @Lazy UserInfoService userInfoService) {
        this.locationService = locationService;
        this.mongoTemplate = mongoTemplate;
        this.likeMapper = likeMapper;
        this.postCache = postCache;
        this.embeddingService = embeddingService;
        this.postStore = postStore;
        this.userInfoService = userInfoService;
    }

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
        userInfoService.incrementPostCount(userId);

        try {
            List<Double> vector = embeddingService.buildDocumentEmbedding(post.getContent());
            postStore.upsert(post.getPostId(), vector);
        } catch (NoApiKeyException e) {
            log.error(e.toString());
        } catch (Exception e) {
            log.error(e.toString());
        }
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

        try {
            postStore.deleteById(postId);
            log.info("帖子向量删除成功 postId={}", postId);
        } catch (Exception e) {
            log.error("帖子向量删除失败 postId={}", postId, e);
        }
    }

    public List<Post> getPostsByPage(Integer from, Integer pageSize) {
        Query query = new Query().skip((long) from).limit(pageSize);
        return mongoTemplate.find(query, Post.class);
    }

    public List<Post> getPostsByPageAndPostType(PostType postType, Integer from, Integer pageSize) {
        Query query = new Query(Criteria.where("postType").is(postType)).skip((long) from).limit(pageSize);
        var posts = mongoTemplate.find(query, Post.class);
        log.info(posts.toString());
        return posts;
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
                Post.class);
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
        List<Like> likes = likeMapper.selectList(new LambdaQueryWrapper<Like>()
                .eq(Like::getLikeType, LikeType.POST_LIKE)
                .eq(Like::getUserId, userId)
                .orderByDesc(Like::getLikeId)
                .last("LIMIT " + n));
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

    public List<PostWithAuthorInfo> getRecommendPostsWithAuthorInfo(Integer userId) throws JsonProcessingException {
        List<String> postIds = postCache.getRecommendPosts(userId);
        if (postIds.isEmpty()) {
            return List.of();
        }

        Query query = Query.query(Criteria.where("postId").in(postIds));
        List<Post> posts = mongoTemplate.find(query, Post.class);

        if (posts.isEmpty()) {
            return List.of();
        }

        List<Integer> authorIds = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, UserInfo> userInfoMap = userInfoService.listByIds(authorIds).stream()
                .collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

        return posts.stream()
                .map(post -> PostWithAuthorInfo.of(post, userInfoMap.get(post.getUserId())))
                .collect(Collectors.toList());
    }

}
