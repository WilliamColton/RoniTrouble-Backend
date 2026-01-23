package org.roni.ronitrouble.component.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.StringCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PostCache extends StringCache {

    private final ObjectMapper objectMapper;

    public void saveRecommendPosts(Integer userId, List<String> postIds) throws JsonProcessingException {
        setValue(String.valueOf(userId), objectMapper.writeValueAsString(postIds), 1, TimeUnit.HOURS);
    }

    public List<String> getRecommendPosts(Integer userId) throws JsonProcessingException {
        String value = getValue(String.valueOf(userId));
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (RuntimeException e) {
            return List.of();
        }
    }

}
