package org.roni.ronitrouble.service;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.post.req.CreateOrUpdatePostReq;
import org.roni.ronitrouble.dto.post.resp.PostAndCuisineInfo;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.enums.LostAndFoundType;
import org.roni.ronitrouble.enums.PostType;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final LocationService locationService;
    private final MongoTemplate mongoTemplate;
    private final CuisineService cuisineService;

    public List<PostAndCuisineInfo> getUserCuisineHistory(Integer userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                        .and("postType").is(PostType.REVIEW)
                        .and("cuisineId").ne(null))
                .with(Sort.by(Sort.Direction.DESC, "score"));
        List<Post> posts = mongoTemplate.find(query, Post.class);

        List<Integer> cuisineIds = posts.stream()
                .map(Post::getCuisineId)
                .distinct()
                .collect(Collectors.toList());

        List<Cuisine> cuisines = cuisineService.listByIds(cuisineIds);
        Map<Integer, Cuisine> cuisineMap = cuisines.stream()
                .collect(Collectors.toMap(Cuisine::getCuisineId, c -> c));

        return posts.stream()
                .map(post -> {
                    Cuisine cuisine = cuisineMap.get(post.getCuisineId());
                    if (cuisine == null) {
                        return null;
                    }
                    PostAndCuisineInfo info = new PostAndCuisineInfo();
                    info.setCuisineName(cuisine.getCuisineName());
                    info.setCuisineIntroduce(cuisine.getIntroduce());
                    info.setUserEvaluation(post.getContent());
                    info.setUserScore(post.getScore());
                    return info;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public List<Post> getPostsByCuisineId(Integer cuisineId) {
        Query query = Query.query(Criteria.where("cuisineId").is(cuisineId));
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
                .set("cuisineId", createOrUpdatePostReq.getCuisineId())
                .set("imageUrls", createOrUpdatePostReq.getImageUrls())
                .set("merchantId", createOrUpdatePostReq.getMerchantId())
                .set("lostAndFoundType", createOrUpdatePostReq.getLostAndFoundType());
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public void delPost(String postId, Integer userId) {
        mongoTemplate.remove(Query.query(Criteria
                .where("postId").is(postId)
                .and("userId").is(userId)), Post.class);
    }

    public List<Post> getPostsByPage(Integer from, Integer pageSize, LostAndFoundType lostAndFoundType) {
        Query query = new Query().skip((long) from).limit(pageSize);
        if (lostAndFoundType != null) {
            query.addCriteria(Criteria.where("lostAndFoundType").is(lostAndFoundType));
        }
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
        if (req.getLostAndFoundType() != null && PostType.LOST_AND_FOUND.equals(req.getPostType())) {
            query.addCriteria(Criteria.where("lostAndFoundType").is(req.getLostAndFoundType()));
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Post.class);
    }

    public Post getPostById(String postId) {
        return mongoTemplate.findById(postId, Post.class);
    }

    public void dislike(String postId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("postId").is(postId).and("likeCount").gt(0)),
                new Update().inc("likeCount", -1),
                Post.class);

    }

}
