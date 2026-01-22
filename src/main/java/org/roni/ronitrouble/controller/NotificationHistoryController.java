package org.roni.ronitrouble.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.entity.NotificationHistory;
import org.roni.ronitrouble.service.NotificationHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification/history")
public class NotificationHistoryController {

    private final NotificationHistoryService notificationHistoryService;

    @Operation(summary = "获取通知历史列表")
    @GetMapping
    public List<NotificationHistory> getNotifications(
            @UserId Integer userId,
            @RequestParam(defaultValue = "1", value = "page") Integer page,
            @RequestParam(defaultValue = "20", value = "size") Integer size) {
        return notificationHistoryService.getNotificationsByUserId(userId, page, size);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unreadCount")
    public Integer getUnreadCount(@UserId Integer userId) {
        return notificationHistoryService.getUnreadCount(userId);
    }

    @Operation(summary = "标记通知为已读")
    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Integer id, @UserId Integer userId) {
        notificationHistoryService.markAsRead(id, userId);
    }

    @Operation(summary = "标记所有通知为已读")
    @PatchMapping("/readAll")
    public void markAllAsRead(@UserId Integer userId) {
        notificationHistoryService.markAllAsRead(userId);
    }
}
