package org.roni.ronitrouble.dto.comment.req;

import lombok.Data;

@Data
public class CreateCommentReq {

    private String postId;
    private String content;

}
