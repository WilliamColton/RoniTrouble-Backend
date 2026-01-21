package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.dto.message.req.SendMessageReq;
import org.roni.ronitrouble.dto.message.resp.MessageResp;
import org.roni.ronitrouble.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public void sendMessage(@RequestBody SendMessageReq sendMessageReq, @UserId Integer userId) {
        messageService.sendMessage(sendMessageReq, userId);
    }

    @GetMapping("/userBox")
    public List<MessageResp> getMessageFromUserBox(@RequestParam("from") Integer mid, @UserId Integer userId) {
        return messageService.getMessageFromUserBox(mid, userId);
    }

}
