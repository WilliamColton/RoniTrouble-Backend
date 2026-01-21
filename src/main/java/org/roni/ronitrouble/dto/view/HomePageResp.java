package org.roni.ronitrouble.dto.view;

import lombok.Data;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;

import java.util.List;

@Data
public class HomePageResp {

    private List<PostDetail> posts;

    @Data
    public static class PostDetail {
        private Post post;
        private UserInfo userInfo;
        private List<Comment> comments;
    }

}
