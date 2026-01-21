package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Comment;
import org.roni.ronitrouble.entity.Like;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.enums.LikeType;
import org.roni.ronitrouble.mapper.UserInfoMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserInfoService extends ServiceImpl<UserInfoMapper, UserInfo> {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    //获取个人资料
    public UserInfo getUserInfo(Integer userId) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId, userId));
        if (userInfo == null) {
            return null;
        }
        if (userInfo.getPostCount() == null) {
            userInfo.setPostCount(0);
        }
        if (userInfo.getViewCount() == null) {
            userInfo.setViewCount(0);
        }
        if (userInfo.getLikesFavoritesCount() == null) {
            userInfo.setLikesFavoritesCount(0);
        }
        return userInfo;
    }

    public Boolean isUserInfoByIdExisted(Integer userId) {
        return Objects.nonNull(getUserInfo(userId));
    }

    public List<UserInfo> listByIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return list(new LambdaQueryWrapper<UserInfo>()
                .in(UserInfo::getUserId, userIds));
    }

    //更新个人资料
    public void addOrUpdateUserInfo(UserInfo userInfo, Integer userId) {
        userInfo.setUserId(userId);
        UserInfo existing = getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId, userId));
        if (existing != null) {
            userInfo.setPostCount(existing.getPostCount());
            userInfo.setViewCount(existing.getViewCount());
            userInfo.setLikesFavoritesCount(existing.getLikesFavoritesCount());
            updateById(userInfo);
        } else {
            if (userInfo.getPostCount() == null) {
                userInfo.setPostCount(0);
            }
            if (userInfo.getViewCount() == null) {
                userInfo.setViewCount(0);
            }
            if (userInfo.getLikesFavoritesCount() == null) {
                userInfo.setLikesFavoritesCount(0);
            }
            save(userInfo);
        }
    }

    //获取用户帖子
    public List<Post> getUserPosts(Integer userId) {
        return postService.getPostsByUserId(userId);
    }

    //获取用户评论
    public List<Comment> getUserComments(Integer userId) {
        return commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserId, userId));
    }

    //获取用户点赞帖子
    public List<Post> getUserLikedPosts(Integer userId) {
        List<Like> likes = likeService.list(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId)
                .eq(Like::getLikeType, LikeType.POST_LIKE));
        if (likes == null || likes.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> postIds = likes.stream()
                .map(Like::getPostId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> posts = new ArrayList<>();
        for (String postId : postIds) {
            Post post = postService.getPostById(postId);
            if (post != null) {
                posts.add(post);
            }
        }
        return posts;
    }

    //统计用户总帖子量
    public Integer getUserPostCount(Integer userId) {
        UserInfo userInfo = getUserInfo(userId);
        return userInfo == null ? 0 : userInfo.getPostCount();
    }

    //统计用户总浏览量
    public Integer getUserViewCount(Integer userId) {
        UserInfo userInfo = getUserInfo(userId);
        return userInfo == null ? 0 : userInfo.getViewCount();
    }

    //统计用户总点赞量
    public Integer getUserLikeCount(Integer userId) {
        UserInfo userInfo = getUserInfo(userId);
        return userInfo == null ? 0 : userInfo.getLikesFavoritesCount();
    }

    public void updateUserStats(Integer userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        int postCount = posts == null ? 0 : posts.size();
        int viewCount = 0;
        int likeCount = 0;
        if (posts != null) {
            for (Post post : posts) {
                if (post == null) {
                    continue;
                }
                if (post.getViewCount() != null) {
                    viewCount += post.getViewCount();
                }
                if (post.getLikeCount() != null) {
                    likeCount += post.getLikeCount();
                }
            }
        }
        update(new LambdaUpdateWrapper<UserInfo>()
                .eq(UserInfo::getUserId, userId)
                .set(UserInfo::getPostCount, postCount)
                .set(UserInfo::getViewCount, viewCount)
                .set(UserInfo::getLikesFavoritesCount, likeCount));
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void refreshAllUserStats() {
        List<UserInfo> users = list();
        if (users == null || users.isEmpty()) {
            return;
        }
        for (UserInfo user : users) {
            if (user == null || user.getUserId() == null) {
                continue;
            }
            updateUserStats(user.getUserId());
        }
    }
    
}
