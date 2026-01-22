package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.roni.ronitrouble.entity.NotificationHistory;
import org.roni.ronitrouble.mapper.NotificationHistoryMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationHistoryService extends ServiceImpl<NotificationHistoryMapper, NotificationHistory> {

    private final UserInfoService userInfoService;

    public NotificationHistoryService(@Lazy UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public void saveNotification(NotificationHistory notification) {
        if (notification.getCreateAt() == null) {
            notification.setCreateAt(java.time.LocalDateTime.now());
        }
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        save(notification);
    }

    public List<NotificationHistory> getNotificationsByUserId(Integer userId, Integer page, Integer size) {
        Page<NotificationHistory> pageParam = new Page<>(page, size);
        Page<NotificationHistory> result = page(pageParam, new LambdaQueryWrapper<NotificationHistory>()
                .eq(NotificationHistory::getUserId, userId)
                .orderByDesc(NotificationHistory::getCreateAt));

        List<NotificationHistory> notifications = result.getRecords();
        for (NotificationHistory notification : notifications) {
            fillOpponentNickname(notification);
        }
        return notifications;
    }

    public Integer getUnreadCount(Integer userId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<NotificationHistory>()
                .eq(NotificationHistory::getUserId, userId)
                .eq(NotificationHistory::getIsRead, false)));
    }

    public void markAsRead(Integer notificationId, Integer userId) {
        update(new LambdaUpdateWrapper<NotificationHistory>()
                .eq(NotificationHistory::getId, notificationId)
                .eq(NotificationHistory::getUserId, userId)
                .set(NotificationHistory::getIsRead, true));
    }

    public void markAllAsRead(Integer userId) {
        update(new LambdaUpdateWrapper<NotificationHistory>()
                .eq(NotificationHistory::getUserId, userId)
                .eq(NotificationHistory::getIsRead, false)
                .set(NotificationHistory::getIsRead, true));
    }

    private void fillOpponentNickname(NotificationHistory notification) {
        if (notification.getOpponentId() != null) {
            var userInfo = userInfoService.getUserInfo(notification.getOpponentId());
            if (userInfo != null) {
                notification.setOpponentId(userInfo.getUserId());
            }
        }
    }
}
