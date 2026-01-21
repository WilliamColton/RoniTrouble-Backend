package org.roni.ronitrouble.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.mapper.PrivateChatMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateChatService {

    private final PrivateChatMapper privateChatMapper;
    private final UserInfoService userInfoService;
    private final ConversationService conversationService;

    public PrivateChatService(PrivateChatMapper privateChatMapper, UserInfoService userInfoService,
            ConversationService conversationService) {
        this.privateChatMapper = privateChatMapper;
        this.userInfoService = userInfoService;
        this.conversationService = conversationService;
    }

    public List<ConversationAndProfileInfo> getConversationAndProfileInfosOfPrivateChat(Integer userId) {
        List<conversationIdAndUserId> ids = privateChatMapper.getConversationAndProfileInfosOfPrivateChat(userId);
        return ids.stream().map(id -> {
            UserInfo userInfo = userInfoService.getUserInfo(id.memberId);
            return ConversationAndProfileInfo.builder()
                    .conversationId(id.conversationId)
                    .userInfo(userInfo)
                    .build();
        }).toList();
    }

    public void createPrivateChat(Integer otherUserId, Integer userId) {
        conversationService.createPrivateConversation(otherUserId, userId);
    }

    public boolean existsPrivateConversation(Integer userId1, Integer userId2) {
        return privateChatMapper.existsPrivateConversation(userId1, userId2) != null;
    }

    @Data
    public static class conversationIdAndUserId {
        private Integer conversationId;
        private Integer memberId;
    }

    @Data
    @Builder
    public static class ConversationAndProfileInfo {
        @JsonUnwrapped
        private UserInfo userInfo;
        private Integer conversationId;
    }

}
