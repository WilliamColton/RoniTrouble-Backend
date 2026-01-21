package org.roni.ronitrouble.component.scheduling;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.entity.Cuisine;
import org.roni.ronitrouble.entity.Merchant;
import org.roni.ronitrouble.entity.Post;
import org.roni.ronitrouble.service.CuisineService;
import org.roni.ronitrouble.service.MerchantService;
import org.roni.ronitrouble.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CountScoreUpdater {

    private final CuisineService cuisineService;
    private final PostService postService;
    private final MerchantService merchantService;

    @Scheduled(cron = "0 0 */1 * * *")
    public void countCuisineScore() {
        List<Cuisine> cuisines = cuisineService.list();
        cuisines.forEach(cuisine -> {
            List<Post> posts = postService.getPostsByCuisineId(cuisine.getCuisineId());
            var score = posts.stream().mapToDouble(Post::getScore).average().orElse(3.5);
            cuisineService.update(new LambdaUpdateWrapper<Cuisine>()
                    .eq(Cuisine::getCuisineId, cuisine.getCuisineId())
                    .set(Cuisine::getScore, score));
        });
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void countMerchant() {
        List<Merchant> merchants = merchantService.list();
        merchants.forEach(merchant -> {
            List<Cuisine> cuisines = cuisineService.list(new LambdaQueryWrapper<Cuisine>()
                    .eq(Cuisine::getMerchantId, merchant.getMerchantId()));
            var score = cuisines.stream().mapToDouble(Cuisine::getScore).average().orElse(3.5);
            merchantService.update(new LambdaUpdateWrapper<Merchant>()
                    .eq(Merchant::getMerchantId, merchant.getMerchantId())
                    .set(Merchant::getScore, score));
        });
    }

}
