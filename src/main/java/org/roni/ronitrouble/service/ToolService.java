package org.roni.ronitrouble.service;

import io.agentscope.core.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.enums.PostType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToolService {

    private final PostService postService;

    @Tool(description = "按帖子类型和分页获取帖子列表。postType可选值：REVIEW（菜品评价贴）、LIFE_STYLE（生活分享贴）、LOST_AND_FOUND（失物招领贴）、TRADE（二手交易贴）。from为起始位置，默认为0。pageSize为每页大小，默认为10")
    public List<Post> getPostsByPageAndCategory(PostType postType, Integer from, Integer pageSize) {
        if (from == null) {
            from = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        var posts = postService.getPostsByPage(from, pageSize);
        return posts.stream().filter(post -> post.getPostType().equals(postType)).toList();
    }

    @Tool(description = "按分页获取帖子列表。from为起始位置，默认为0。pageSize为每页大小，默认为10")
    public List<Post> getPostsByPage(Integer from, Integer pageSize) {
        if (from == null) {
            from = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return postService.getPostsByPage(from, pageSize);
    }

}
