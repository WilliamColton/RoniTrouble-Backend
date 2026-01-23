package org.roni.ronitrouble.component.scheduling;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.component.cache.impl.PostCache;
import org.roni.ronitrouble.component.store.vectorStore.MilvusStore;
import org.roni.ronitrouble.component.store.vectorStore.impl.PostStore;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.mapper.UserInfoMapper;
import org.roni.ronitrouble.service.AIService;
import org.roni.ronitrouble.service.EmbeddingService;
import org.roni.ronitrouble.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileSummaryService {

    private static final int RECENT_POSTS_COUNT = 10;
    private static final int RECOMMEND_POSTS_LIMIT = 20;
    private final PostService postService;
    private final AIService aiService;
    private final EmbeddingService embeddingService;
    private final PostStore postStore;
    private final PostCache postCache;
    private final UserInfoMapper userInfoMapper;

    @Scheduled(cron = "*/15 * * * * *")
    public void generateUserProfileSummaries() {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        List<UserInfo> users = userInfoMapper.selectList(queryWrapper);

        for (UserInfo user : users) {
            try {
                generateSummaryForUser(user.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void generateSummaryForUser(Integer userId) throws NoApiKeyException, JsonProcessingException {
        List<Post> recentPosts = postService.getRecentPostsByUserId(userId, RECENT_POSTS_COUNT);
        List<Post> recentLikedPosts = postService.getRecentLikedPosts(userId, RECENT_POSTS_COUNT);

        String content = buildContentString(recentPosts, recentLikedPosts);

        if (content.isEmpty()) {
            log.info("用户暂无帖子和点赞记录 userId={}", userId);
            return;
        }

        String summary = aiService.generateProfileQuery(content);

        List<Double> vector = embeddingService.buildProfileDocumentEmbedding(summary);

        var results = postStore.listSimilarResultsByVector(vector, RECOMMEND_POSTS_LIMIT, 0.35f);

        List<String> similarPostIds = results.stream()
                .map(r -> String.valueOf(r.getId()))
                .toList();

        postCache.saveRecommendPosts(userId, similarPostIds);
    }

    private String buildContentString(List<Post> posts, List<Post> likedPosts) {
        StringBuilder sb = new StringBuilder();

        if (!posts.isEmpty()) {
            sb.append("\n【最近发布的帖子】\n");
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                sb.append(i + 1).append(". ").append(post.getContent()).append("\n");
            }
        }

        if (!likedPosts.isEmpty()) {
            sb.append("\n【最近点赞的帖子】\n");
            for (int i = 0; i < likedPosts.size(); i++) {
                Post post = likedPosts.get(i);
                sb.append(i + 1).append(". ").append(post.getContent()).append("\n");
            }
        }

        return sb.toString();
    }
}
