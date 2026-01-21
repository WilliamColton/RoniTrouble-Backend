package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.merchant.resp.MerchantReviewWithUserInfo;
import org.roni.ronitrouble.entity.Merchant;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.entity.UserInfo;
import org.roni.ronitrouble.enums.PostType;
import org.roni.ronitrouble.mapper.MerchantMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {

    private final MongoTemplate mongoTemplate;
    private final UserInfoService userInfoService;

    // 获取全部商家信息并按评分排序
    public List<Merchant> getMerchantInfosByPage(Integer start, Integer pageSize) {
        Page<Merchant> merchantPage = new Page<>(start, pageSize);
        return page(merchantPage, new LambdaQueryWrapper<Merchant>()
                .orderByDesc(Merchant::getScore, Merchant::getCreatedAt)).getRecords();
    }

    // 获取单个商家信息
    public Merchant getMerchantInfoById(Integer merchantId) {
        return getById(merchantId);
    }

    // 商家信息是否存在
    public Boolean isMerchantInfoIsExisted(Integer merchantId) {
        return getById(merchantId) != null;
    }

    // 新建或更新商家信息
    public void addOrUpdateMerchantInfoById(Merchant merchant, Integer merchantId) {
        merchant.setMerchantId(merchantId);
        if (isMerchantInfoIsExisted(merchantId)) {
            updateById(merchant);
        } else {
            merchant.setScore(3.5);
            save(merchant);
        }
    }

    // 删除商家信息
    public void deleteMerchantById(Integer merchantId) {
        remove(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantId, merchantId));
    }

    public List<MerchantReviewWithUserInfo> getMerchantReviewsWithUserInfo(Integer merchantId) {
        Query query = Query.query(Criteria.where("merchantId").is(merchantId)
                .and("postType").is(PostType.REVIEW))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = mongoTemplate.find(query, Post.class);

        List<Integer> userIds = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<UserInfo> userInfos = userInfoService.listByIds(userIds);
        Map<Integer, UserInfo> userInfoMap = userInfos.stream()
                .collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

        return posts.stream()
                .map(post -> {
                    MerchantReviewWithUserInfo info = new MerchantReviewWithUserInfo();
                    info.setPost(post);
                    info.setUserInfo(userInfoMap.get(post.getUserId()));
                    return info;
                })
                .collect(Collectors.toList());
    }

}