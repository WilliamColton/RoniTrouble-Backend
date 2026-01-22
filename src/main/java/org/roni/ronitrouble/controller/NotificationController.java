package org.roni.ronitrouble.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.exception.BusinessException;
import org.roni.ronitrouble.exception.exceptions.AuthError;
import org.roni.ronitrouble.service.NotificationService;
import org.roni.ronitrouble.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtCache jwtCache;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter buildMessageNotificationChannel(@RequestParam("token") String token) throws JsonProcessingException {
        JwtUtil.UserCredentialInfo userCredentialInfo;
        if (isTheTokenInTheCache(token)) {
            userCredentialInfo = jwtCache.getUserCredentialInfo(token);
        } else if (JwtUtil.validate(token)) {
            userCredentialInfo = JwtUtil.getUserCredentialInfo(token);
        } else {
            throw new BusinessException(AuthError.NO_AUTH);
        }
        return notificationService.buildMessageNotificationChannel(userCredentialInfo.userId());
    }

    public boolean isTheTokenInTheCache(String token) {
        try {
            return jwtCache.getUserCredentialInfo(token) != null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
