package org.roni.ronitrouble.dto.friendApply.resp;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.roni.ronitrouble.entity.FriendApply;
import org.roni.ronitrouble.entity.UserInfo;

@Data
public class ApplyAndProfileInfo {

    @JsonUnwrapped
    private FriendApply friendApply;
    @JsonUnwrapped
    private UserInfo userInfo;

    public static ApplyAndProfileInfo ofUserInfoAndApply(UserInfo userInfo, FriendApply friendApply) {
        ApplyAndProfileInfo applyAndProfileInfo = new ApplyAndProfileInfo();
        applyAndProfileInfo.setFriendApply(friendApply);
        applyAndProfileInfo.setUserInfo(userInfo);
        return applyAndProfileInfo;
    }

}
