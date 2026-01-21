package org.roni.ronitrouble.dto.post.req;

import lombok.Data;
import org.roni.ronitrouble.enums.LostAndFoundType;
import org.roni.ronitrouble.enums.PostType;

@Data
public class CreateOrUpdatePostReq {

    private PostType postType;
    private String postId;
    private String content;

    private Integer cuisineId;
    private Integer merchantId;

    private LostAndFoundType lostAndFoundType;

}
