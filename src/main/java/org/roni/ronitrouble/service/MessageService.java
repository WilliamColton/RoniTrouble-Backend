package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.dto.message.req.SendMessageReq;
import org.roni.ronitrouble.dto.message.resp.MessageResp;
import org.roni.ronitrouble.entity.Message;
import org.roni.ronitrouble.mapper.MessageMapper;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    private final ConversationMemberService conversationMemberService;
    private final ConversationService conversationService;
    private final UserCredentialService userCredentialService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final MessageStoreService messageStoreService;

    @Transactional
    public void sendMessage(SendMessageReq sendMessageReq, Integer userId) {
        Message message = MapperUtil.mapper(Message.class, sendMessageReq);
        message.setSessionMid(conversationService.getMaxMidByConversationId(sendMessageReq.getConversationId()));
        message.setSenderId(userId);
        save(message);
        conversationService.addMaxMidByConversationId(sendMessageReq.getConversationId());
        List<Integer> ids = conversationMemberService
                .getMemberIdsOfChatByConversationId(sendMessageReq.getConversationId());

        for (Integer id : ids) {
            if (id == 0)
                continue;
            Integer userMaxMid = userCredentialService.getCurrentMaxMidByUserId(id);
            userCredentialService.addCurrentMixMidByUserId(userMaxMid, id);
            userMaxMid++;
            notificationService.sendNotification(id, NotificationService.Notification.builder()
                    .notificationType(NotificationService.NotificationType.MessageNotification).build());
            messageStoreService.saveMessageInTheTimeline(userMaxMid, message, id);
        }
    }

    public List<MessageResp> getMessageFromUserBox(Integer mid, Integer userId) {
        return redisTemplate.opsForZSet().rangeByScore("user_box:" + userId, mid + 1, Double.MAX_VALUE).stream()
                .map(v -> {
                    try {
                        return objectMapper.readValue(v, MessageResp.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

}
