package org.roni.ronitrouble.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.service.AIService;
import org.roni.ronitrouble.util.UserContextUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final JwtCache jwtCache;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam("message") String message, @RequestParam("token") String token) throws JsonProcessingException {
        var userCredentialInfo = jwtCache.getUserCredentialInfo(token);
        return aiService.chat(message, userCredentialInfo.userId());
    }

}