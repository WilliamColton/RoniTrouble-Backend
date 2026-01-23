package org.roni.ronitrouble.dto.post.resp;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;

@Data
public class PostWithAuthorInfo {

    @JsonUnwrapped
    private Post post;

    @JsonUnwrapped
    private UserInfo authorInfo;

    public static PostWithAuthorInfo of(Post post, UserInfo authorInfo) {
        PostWithAuthorInfo result = new PostWithAuthorInfo();
        result.setPost(post);
        result.setAuthorInfo(authorInfo);
        return result;
    }

}