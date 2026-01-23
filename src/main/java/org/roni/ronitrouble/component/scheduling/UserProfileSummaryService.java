package org.roni.ronitrouble.component.scheduling;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.component.cache.impl.PostCache;
import org.roni.ronitrouble.component.store.vectorStore.impl.PostStore;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.mapper.UserInfoMapper;
import org.roni.ronitrouble.service.AIService;
import org.roni.ronitrouble.service.EmbeddingService;
import org.roni.ronitrouble.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Scheduled(cron = "0 */5 * * * *")
    public void generateUserProfileSummaries() {
        log.info("开始生成用户画像总结...");

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        List<UserInfo> users = userInfoMapper.selectList(queryWrapper);

        log.info("待处理用户数量: {}", users.size());

        int successCount = 0;
        int failCount = 0;

        for (UserInfo user : users) {
            try {
                generateSummaryForUser(user.getUserId());
                successCount++;
            } catch (Exception e) {
                log.error("生成用户画像总结失败 userId={}, 错误={}", user.getUserId(), e, e);
                failCount++;
            }
        }

        log.info("用户画像总结生成完成, 成功={}, 失败={}", successCount, failCount);
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

        List<String> similarPostIds = postStore.listSimilarResultsByVector(vector, RECOMMEND_POSTS_LIMIT)
                .stream()
                .map(r -> String.valueOf(r.getId()))
                .toList();

        postCache.saveRecommendPosts(userId, similarPostIds);

        log.info("用户画像处理完成 userId={}, 推荐帖子数={}, summery={}", userId, similarPostIds.size(), summary);
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
