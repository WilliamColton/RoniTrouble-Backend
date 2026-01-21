package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.roni.ronitrouble.enums.Role;

/**
 * <p>
 * 鉴权表
 * </p>
 *
 * @author FanQis
 * @since 2025-09-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_credentials")
public class UserCredential {

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 校园邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    private Integer currentMaxMid;

    private Role role;

}
