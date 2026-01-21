package org.roni.ronitrouble.entity;

import lombok.Data;

@Data
public class Cuisine {
    private Integer cuisineId;
    private Integer merchantId;
    private String avatarUrl;
    private String cuisineName;
    private Double price;
    private Double score;
    private String introduce;
}
