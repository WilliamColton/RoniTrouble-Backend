package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.roni.ronitrouble.mapper.CuisineMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuisineService extends ServiceImpl<CuisineMapper, Cuisine> {

    //获取某商家的所有菜品并按评分排序
    public List<Cuisine> getCuisinesByMerchant(Integer merchantId) {
        return list(new LambdaQueryWrapper<Cuisine>()
                .eq(Cuisine::getMerchantId, merchantId)
                .orderByDesc(Cuisine::getScore));
    }

    //菜品是否存在
    public Boolean isCuisineExisted(Integer cuisineId) {
        return getById(cuisineId) != null;
    }

    //新增或修改菜品
    public void addOrUpdateCuisine(Cuisine cuisine, Integer merchantId) {
        cuisine.setMerchantId(merchantId);
        if (cuisine.getCuisineId() == null || !isCuisineExisted(cuisine.getCuisineId())) {
            save(cuisine);
        } else {
            updateById(cuisine);
        }
    }

    //删除菜品
    public void deleteCuisine(Integer cuisineId) {
        remove(new LambdaQueryWrapper<Cuisine>()
                .eq(Cuisine::getCuisineId, cuisineId));
    }

}
