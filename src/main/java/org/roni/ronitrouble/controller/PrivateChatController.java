package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.service.PrivateChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/privateChat")
public class PrivateChatController {

    private final PrivateChatService privateChatService;

    @GetMapping
    public List<PrivateChatService.ConversationAndProfileInfo> getProfilesOfPrivateChat(@UserId Integer userId) {
        return privateChatService.getConversationAndProfileInfosOfPrivateChat(userId);
    }

}
