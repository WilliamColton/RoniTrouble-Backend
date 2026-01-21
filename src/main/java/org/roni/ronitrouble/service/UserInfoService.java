package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserInfoService extends ServiceImpl<UserInfoMapper, UserInfo> {
    //获取个人资料
    public UserInfo getUserInfo(Integer userId) {
        return getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId, userId));
    }

    public Boolean isUserInfoByIdExisted(Integer userId) {
        return Objects.nonNull(getUserInfo(userId));
    }

    public List<UserInfo> listByIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return list(new LambdaQueryWrapper<UserInfo>()
                .in(UserInfo::getUserId, userIds));
    }

    //更新个人资料
    public void addOrUpdateUserInfo(UserInfo userInfo, Integer userId) {
        if (isUserInfoByIdExisted(userId)) {
            //更新
            userInfo.setUserId(userId);
            updateById(userInfo);
        } else {
            //新建
            userInfo.setUserId(userId);
            save(userInfo);
        }
    }
}
