package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Conversation;
import org.roni.ronitrouble.mapper.ConversationMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService extends ServiceImpl<ConversationMapper, Conversation> {

    private final ConversationMemberService conversationMemberService;

    public Integer getMaxMidByConversationId(Integer conversationId) {
        return getOneOpt(new LambdaQueryWrapper<Conversation>()
                .select(Conversation::getCurrentMaxMid)
                .eq(Conversation::getConversationId, conversationId)).orElseThrow().getCurrentMaxMid();
    }

    public void createPrivateConversation(Integer otherUserId, Integer userId) {
        Conversation conversation = new Conversation();
        conversation.setCurrentMaxMid(0);
        save(conversation);
        conversationMemberService.createConversationMember(conversation.getConversationId(), userId);
        conversationMemberService.createConversationMember(conversation.getConversationId(), otherUserId);
    }

    public void addMaxMidByConversationId(Integer conversationId) {
        update(new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getConversationId, conversationId)
                .set(Conversation::getCurrentMaxMid, getMaxMidByConversationId(conversationId) + 1));
    }

}
