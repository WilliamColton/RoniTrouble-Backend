package org.roni.ronitrouble.dto.message.resp;

import lombok.Data;
import org.roni.ronitrouble.enums.MessageType;

@Data
public class MessageResp {

    private Integer mid;
    private String content;
    private Integer senderId;
    private Integer conversationId;
    private MessageType messageType;
    private Integer midForUser;

}
