package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.roni.ronitrouble.enums.FriendApplyStatus;
import org.roni.ronitrouble.enums.ReadStatus;

import java.time.LocalDateTime;

@Data
@TableName("friend_applies")
public class FriendApply {

    @TableId(type = IdType.AUTO)
    Integer id;
    Integer senderId;
    Integer receiverId;
    String msg;
    FriendApplyStatus friendApplyStatus;
    ReadStatus readStatus;
    LocalDateTime createdAt;

}
