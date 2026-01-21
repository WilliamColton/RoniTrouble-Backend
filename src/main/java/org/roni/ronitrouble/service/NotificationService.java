package org.roni.ronitrouble.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final ConcurrentHashMap<Integer, Set<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();

    @Scheduled(cron = "*/3 * * * * *")
    public void heart() {
        for (var entry : sseEmitterMap.entrySet()) {
            Integer userId = entry.getKey();
            Set<SseEmitter> set = entry.getValue();

            List<SseEmitter> snapshot = List.copyOf(set);

            for (SseEmitter emitter : snapshot) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("heart")
                            .data("."));
                } catch (IOException | IllegalStateException e) {
                    log.warn("SSE心跳发送失败，移除连接 userId={}, 原因={}", userId, e.toString());
                    safeComplete(emitter);
                    set.remove(emitter);
                }
            }

            if (set.isEmpty()) {
                sseEmitterMap.remove(userId, set);
            }
        }
    }

    public SseEmitter buildMessageNotificationChannel(Integer userId) {
        SseEmitter emitter = new SseEmitter(0L);

        Set<SseEmitter> set = sseEmitterMap.computeIfAbsent(
                userId, k -> ConcurrentHashMap.newKeySet()
        );
        set.add(emitter);
        log.info("建立SSE连接 userId={}, 连接数={}", userId, set.size());

        Runnable cleanup = () -> {
            set.remove(emitter);
            if (set.isEmpty()) {
                sseEmitterMap.remove(userId, set);
            }
            log.info("关闭SSE连接 userId={}, 剩余连接数={}", userId, set.size());
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时 userId={}", userId);
            safeComplete(emitter);
            cleanup.run();
        });
        emitter.onError(e -> {
            log.warn("SSE连接异常 userId={}, 原因={}", userId, e.toString());
            safeCompleteWithError(emitter, e);
            cleanup.run();
        });

        return emitter;
    }

    public void sendNotification(Integer userId, Notification notification) {
        Set<SseEmitter> set = sseEmitterMap.get(userId);
        if (set == null || set.isEmpty()) return;

        List<SseEmitter> snapshot = List.copyOf(set);

        for (SseEmitter emitter : snapshot) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notification, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException e) {
                log.warn("SSE发送通知失败，移除连接 userId={}, 原因={}", userId, e.toString());
                safeComplete(emitter);
                set.remove(emitter);
            }
        }

        if (set.isEmpty()) {
            sseEmitterMap.remove(userId, set);
        }
    }

    private void safeComplete(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (IllegalStateException ignore) {
        }
    }

    private void safeCompleteWithError(SseEmitter emitter, Throwable e) {
        try {
            emitter.completeWithError(e);
        } catch (IllegalStateException ignore) {
        }
    }

    public enum NotificationType {
        MessageNotification
    }

    @Data
    @Builder
    public static class Notification {
        private NotificationType notificationType;
        private Object body;
    }
}
