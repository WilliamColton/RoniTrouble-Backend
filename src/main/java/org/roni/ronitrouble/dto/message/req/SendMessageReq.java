package org.roni.ronitrouble.dto.message.req;

import lombok.Data;
import org.roni.ronitrouble.enums.MessageType;

@Data
public class SendMessageReq {

    private String content;
    private Integer conversationId;
    private MessageType messageType;
    private String rawFileName;

}
