package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.roni.ronitrouble.entity.Merchant;
import org.roni.ronitrouble.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {
    //获取全部商家信息并按评分排序
    public List<Merchant> getMerchantInfosByPage(Integer start, Integer pageSize) {
        Page<Merchant> merchantPage = new Page<>(start, pageSize);
        return page(merchantPage, new LambdaQueryWrapper<Merchant>()
                .orderByDesc(Merchant::getScore, Merchant::getCreatedAt)).getRecords();
    }

    //获取单个商家信息
    public Merchant getMerchantInfoById(Integer merchantId) {
        return getById(merchantId);
    }

    //商家信息是否存在
    public Boolean isMerchantInfoIsExisted(Integer merchantId) {
        return getById(merchantId) != null;
    }

    //新建或更新商家信息
    public void addOrUpdateMerchantInfoById(Merchant merchant, Integer merchantId) {
        merchant.setMerchantId(merchantId);
        if (isMerchantInfoIsExisted(merchantId)) {
            updateById(merchant);
        } else {
            merchant.setScore(3.5);
            save(merchant);
        }
    }

    //删除商家信息
    public void deleteMerchantById(Integer merchantId) {
        remove(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantId, merchantId));
    }
}