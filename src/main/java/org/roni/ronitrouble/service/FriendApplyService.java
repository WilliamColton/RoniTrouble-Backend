package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.roni.ronitrouble.dto.friendApply.req.ChangeFriendApplyReq;
import org.roni.ronitrouble.dto.friendApply.req.ChangeReadStatusReq;
import org.roni.ronitrouble.dto.friendApply.req.CreateFriendApplyReq;
import org.roni.ronitrouble.dto.friendApply.resp.ApplyAndProfileInfo;
import org.roni.ronitrouble.entity.FriendApply;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.enums.FriendApplyStatus;
import org.roni.ronitrouble.enums.ReadStatus;
import org.roni.ronitrouble.exception.BusinessException;
import org.roni.ronitrouble.exception.exceptions.OtherError;
import org.roni.ronitrouble.mapper.FriendApplyMapper;
import org.roni.ronitrouble.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendApplyService extends ServiceImpl<FriendApplyMapper, FriendApply> {

    private final UserInfoService userInfoService;
    private final ConversationService conversationService;
    private final PrivateChatService privateChatService;

    public FriendApplyService(UserInfoService userInfoService, ConversationService conversationService,
                              PrivateChatService privateChatService) {
        this.userInfoService = userInfoService;
        this.conversationService = conversationService;
        this.privateChatService = privateChatService;
    }

    public void createFriendApply(CreateFriendApplyReq createFriendApplyReq, Integer userId) {
        FriendApply friendApply = MapperUtil.mapper(FriendApply.class, createFriendApplyReq);
        friendApply.setFriendApplyStatus(FriendApplyStatus.NOT_READ);
        friendApply.setReadStatus(ReadStatus.NOT_READ);
        friendApply.setSenderId(userId);
        save(friendApply);
    }

    @Transactional
    public void changeFriendApply(ChangeFriendApplyReq changeFriendApplyReq, Integer userId) {
        FriendApply friendApply = getOneOpt(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getId, changeFriendApplyReq.getId()))
                .orElseThrow(() -> new BusinessException(OtherError.NETWORK_ERROR));

        if (!friendApply.getReceiverId().equals(userId)) {
            throw new BusinessException(OtherError.NETWORK_ERROR);
        }

        update(new LambdaUpdateWrapper<FriendApply>()
                .eq(FriendApply::getId, changeFriendApplyReq.getId())
                .set(FriendApply::getFriendApplyStatus, changeFriendApplyReq.getFriendApplyStatus()));

        if (changeFriendApplyReq.getFriendApplyStatus() == FriendApplyStatus.PASSED) {
            if (privateChatService.existsPrivateConversation(userId, friendApply.getSenderId())) {
                throw new BusinessException(OtherError.NETWORK_ERROR);
            }
            conversationService.createPrivateConversation(friendApply.getSenderId(), userId);
        }
    }

    public Integer getNotReadCount(Integer userId) {
        return list(
                new LambdaQueryWrapper<FriendApply>()
                        .eq(FriendApply::getReceiverId, userId)
                        .eq(FriendApply::getReadStatus, ReadStatus.NOT_READ))
                .size();
    }

    public List<ApplyAndProfileInfo> getApplyAndProfileInfos(Integer userId) {
        return list(
                new LambdaQueryWrapper<FriendApply>()
                        .eq(FriendApply::getReceiverId, userId))
                .stream().map(friendApply -> {
                    UserInfo userInfo = userInfoService.getUserInfo(friendApply.getSenderId());
                    return ApplyAndProfileInfo.ofUserInfoAndApply(userInfo, friendApply);
                }).toList();
    }

    public void changeReadStatus(ChangeReadStatusReq changeReadStatusReq) {
        update(new LambdaUpdateWrapper<FriendApply>()
                .eq(FriendApply::getId, changeReadStatusReq.getId())
                .set(FriendApply::getReadStatus, changeReadStatusReq.getReadStatus()));
    }

}
