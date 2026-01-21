package org.roni.ronitrouble.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.roni.ronitrouble.service.PrivateChatService;

import java.util.List;

@Mapper
public interface PrivateChatMapper {

    @Select("""
                SELECT  c.conversation_id,
                    cm_other.member_id
                FROM conversation_members AS cm_me
                INNER JOIN conversations c ON cm_me.conversation_id = c.conversation_id
                INNER JOIN conversation_members cm_other ON cm_me.conversation_id = cm_other.conversation_id AND cm_me.member_id != cm_other.member_id
                WHERE cm_me.member_id = ${userId};
            """)
    List<PrivateChatService.conversationIdAndUserId> getConversationAndProfileInfosOfPrivateChat(Integer userId);

    @Select("""
                SELECT 1
                FROM conversation_members cm1
                INNER JOIN conversation_members cm2 ON cm1.conversation_id = cm2.conversation_id
                INNER JOIN conversations c ON cm1.conversation_id = c.conversation_id
                WHERE cm1.member_id = #{userId1} AND cm2.member_id = #{userId2}
                LIMIT 1
            """)
    Integer existsPrivateConversation(Integer userId1, Integer userId2);

}
