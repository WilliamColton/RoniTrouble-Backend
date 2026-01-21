package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.service.CuisineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuisine")
@RequiredArgsConstructor
public class CuisineController {
    private final CuisineService cuisineService;

    //获取某商家的所有菜品并按评分排序
    @GetMapping("/cuisines")
    public List<Cuisine> getCuisinesByMerchant(@RequestParam Integer merchantId) {
        return cuisineService.getCuisinesByMerchant(merchantId);
    }

    //新增或修改菜品
    @PostMapping("/update")
    public void addOrUpdateCuisine(@RequestBody Cuisine cuisine, @RequestParam Integer merchantId) {
        cuisineService.addOrUpdateCuisine(cuisine, merchantId);
    }

    //删除菜品
    @DeleteMapping("/delete")
    public void deleteCuisine(@RequestParam Integer cuisineId) {
        cuisineService.deleteCuisine(cuisineId);
    }

}
