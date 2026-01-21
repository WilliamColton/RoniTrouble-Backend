package org.roni.ronitrouble.service;

import com.alibaba.dashscope.exception.NoApiKeyException;
import io.agentscope.core.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.store.vectorStore.impl.CuisineStore;
import org.roni.ronitrouble.dto.post.resp.PostAndCuisineInfo;
import org.roni.ronitrouble.entity.Cuisine;
import org.roni.ronitrouble.entity.UserCredential;
import org.roni.ronitrouble.util.UserContextUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToolService {

    private final UserCredentialService userCredentialService;
    private final PostService postService;
    private final AIService aiService;
    private final EmbeddingService embeddingService;
    private final CuisineStore cuisineStore;
    private final CuisineService cuisineService;

    @Tool
    public List<Cuisine> getRecommendedCuisinesOfCurrentUser() {
        Integer userId = UserContextUtil.getCurrentUserId();
        UserCredential userCredential = userCredentialService.getById(userId);
        StringBuilder sb = new StringBuilder();
        List<PostAndCuisineInfo> histories = postService.getUserCuisineHistory(userCredential.getUserId());
        histories.forEach(history -> sb.append(history.buildQuery()));
        String query = aiService.generateProfileQuery(sb.toString());
        try {
            List<Double> queryVector = embeddingService.buildQueryEmbedding(query,
                    "Given a user dietary preference query, retrieve the dishes that the user is most likely to prefer.");
            return cuisineStore.listSimilarResultsByVector(queryVector, 10).stream()
                    .map(result -> cuisineService.getById(result.getId()))
                    .toList();
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
