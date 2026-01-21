package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.roni.ronitrouble.enums.MessageType;

@Data
@TableName("messages")
public class Message {

    @TableId(type = IdType.AUTO)
    private Integer mid;
    private Integer sessionMid;
    private String content;
    private Integer senderId;
    private Integer conversationId;
    private MessageType messageType;
    private String rawFileName;

}
