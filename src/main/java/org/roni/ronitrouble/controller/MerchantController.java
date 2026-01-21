package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.dto.merchant.resp.MerchantReviewWithUserInfo;
import org.roni.ronitrouble.entity.Merchant;
import org.roni.ronitrouble.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    //获取全部商家信息并按评分排序
    @GetMapping("/page")
    public List<Merchant> getMerchantInfosByPage(@RequestParam Integer start, @RequestParam Integer pageSize) {
        return merchantService.getMerchantInfosByPage(start, pageSize);
    }

    //获取单个商家信息
    @GetMapping("/merchantInfo")
    public Merchant getMerchantInfoById(@UserId Integer merchantId) {
        return merchantService.getMerchantInfoById(merchantId);
    }

    //新建或更新商家信息
    @PostMapping("/update")
    public void addOrUpdateMerchantInfoById(@RequestBody Merchant merchant, @UserId Integer merchantId) {
        merchantService.addOrUpdateMerchantInfoById(merchant, merchantId);
    }

    //删除商家信息
    @DeleteMapping("/delete")
    public void deleteMerchantById(@UserId Integer merchantId) {
        merchantService.deleteMerchantById(merchantId);
    }

    //获取商家评价及用户信息
    @GetMapping("/reviews")
    public List<MerchantReviewWithUserInfo> getMerchantReviewsWithUserInfo(@UserId Integer merchantId) {
        return merchantService.getMerchantReviewsWithUserInfo(merchantId);
    }
}
