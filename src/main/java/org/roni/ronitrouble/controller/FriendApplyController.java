package org.roni.ronitrouble.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.dto.friendApply.req.ChangeFriendApplyReq;
import org.roni.ronitrouble.dto.friendApply.req.ChangeReadStatusReq;
import org.roni.ronitrouble.dto.friendApply.req.CreateFriendApplyReq;
import org.roni.ronitrouble.dto.friendApply.resp.ApplyAndProfileInfo;
import org.roni.ronitrouble.service.FriendApplyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friendApply")
public class FriendApplyController {

    private final FriendApplyService friendApplyService;

    @PostMapping
    public void createFriendApply(@RequestBody CreateFriendApplyReq createFriendApplyReq, @UserId Integer userId) {
        friendApplyService.createFriendApply(createFriendApplyReq, userId);
    }

    @Operation(summary = "修改好友申请通过状态")
    @PatchMapping
    public void changeFriendApplyStatus(@RequestBody ChangeFriendApplyReq changeFriendApplyReq, @UserId Integer userId) {
        friendApplyService.changeFriendApply(changeFriendApplyReq, userId);
    }

    @Operation(summary = "修改好友申请已读未读状态")
    @PatchMapping("/readStatus")
    public void changeReadStatus(@RequestBody ChangeReadStatusReq changeReadStatusReq) {
        friendApplyService.changeReadStatus(changeReadStatusReq);
    }

    @Operation(summary = "获取未读好友申请数")
    @GetMapping("/count")
    public Integer getNotReadCount(@UserId Integer userId) {
        return friendApplyService.getNotReadCount(userId);
    }

    @GetMapping
    public List<ApplyAndProfileInfo> getApplyAndProfileInfos(@UserId Integer userId) {
        return friendApplyService.getApplyAndProfileInfos(userId);
    }

}
