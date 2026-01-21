package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.ConversationMember;
import org.roni.ronitrouble.mapper.ConversationMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationMemberService extends ServiceImpl<ConversationMemberMapper, ConversationMember> {

    public List<Integer> getMemberIdsOfChatByConversationId(Integer conversationId) {
        return list(new LambdaQueryWrapper<ConversationMember>()
                .select(ConversationMember::getMemberId)
                .eq(ConversationMember::getConversationId, conversationId)).stream().map(ConversationMember::getMemberId).toList();
    }

    public void createConversationMember(Integer conversationId, Integer userId) {
        ConversationMember conversationMember = new ConversationMember();
        conversationMember.setConversationId(conversationId);
        conversationMember.setMemberId(userId);
        save(conversationMember);
    }

}
