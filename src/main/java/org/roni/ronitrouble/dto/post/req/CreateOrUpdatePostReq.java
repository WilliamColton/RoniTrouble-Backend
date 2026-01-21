package org.roni.ronitrouble.dto.post.req;

import lombok.Data;
import org.roni.ronitrouble.enums.PostType;

import java.util.List;

@Data
public class CreateOrUpdatePostReq {

    private PostType postType;
    private String postId;
    private String content;
    private List<String> imageUrls;

}
