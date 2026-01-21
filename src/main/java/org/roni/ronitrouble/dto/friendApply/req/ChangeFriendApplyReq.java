package org.roni.ronitrouble.dto.friendApply.req;

import lombok.Data;
import org.roni.ronitrouble.enums.FriendApplyStatus;

@Data
public class ChangeFriendApplyReq {

    private Integer id;
    private FriendApplyStatus friendApplyStatus;

}
