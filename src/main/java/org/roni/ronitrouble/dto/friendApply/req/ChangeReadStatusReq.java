package org.roni.ronitrouble.dto.friendApply.req;

import lombok.Data;
import org.roni.ronitrouble.enums.ReadStatus;

@Data
public class ChangeReadStatusReq {

    private Integer id;
    private ReadStatus readStatus;

}
