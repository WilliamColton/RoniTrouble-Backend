package org.roni.ronitrouble.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.message.resp.MessageResp;
import org.roni.ronitrouble.entity.Message;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageStoreService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveMessageInTheTimeline(Integer maxUserMid, Message message, Integer userId) {
        try {
            MessageResp messageResp = MapperUtil.mapper(MessageResp.class, message);
            messageResp.setMidForUser(maxUserMid);
            redisTemplate.opsForZSet().add("user_box:" + userId, objectMapper.writeValueAsString(messageResp), maxUserMid);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
