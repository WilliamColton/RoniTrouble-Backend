package org.roni.ronitrouble.dto.post.resp;

import lombok.Data;

@Data
public class PostAndCuisineInfo {
    private String cuisineName;
    private String cuisineIntroduce;
    private String userEvaluation;
    private Double userScore;

    public String buildQuery() {
        return "菜品名：" + cuisineName + ",菜品简介:" + cuisineIntroduce + ",用户对菜品的评价:" + userEvaluation + ",用户的打分：" + userScore + '\n';
    }
}
