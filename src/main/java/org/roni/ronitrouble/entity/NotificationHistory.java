package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.roni.ronitrouble.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notification_histories")
public class NotificationHistory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 记录归属者
    private Integer userId;

    // 记录谁给你点的赞
    private Integer opponentId;

    private NotificationType notificationType;

    private String content;

    private String postId;

    private Integer commentId;

    private Integer friendApplyId;

    private LocalDateTime createAt;

    private Boolean isRead;
}
