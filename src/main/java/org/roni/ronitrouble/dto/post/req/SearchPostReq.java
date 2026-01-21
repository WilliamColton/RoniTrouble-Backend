package org.roni.ronitrouble.dto.post.req;

import lombok.Data;
import org.roni.ronitrouble.enums.LostAndFoundType;
import org.roni.ronitrouble.enums.PostType;

@Data
public class SearchPostReq {

    private String keyword;
    private PostType postType;
    private LostAndFoundType lostAndFoundType;

}
