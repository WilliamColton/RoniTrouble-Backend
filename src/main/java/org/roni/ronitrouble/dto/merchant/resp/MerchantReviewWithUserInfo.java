package org.roni.ronitrouble.dto.merchant.resp;

import lombok.Data;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;

@Data
public class MerchantReviewWithUserInfo {
    private Post post;
    private UserInfo userInfo;
}
