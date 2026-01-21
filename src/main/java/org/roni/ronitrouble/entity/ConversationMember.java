package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("conversation_members")
public class ConversationMember {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer conversationId;
    private Integer memberId;

}
