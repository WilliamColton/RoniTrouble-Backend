package org.roni.ronitrouble.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Cuisine {
    @TableId(value = "cuisine_id", type = IdType.AUTO)
    private Integer cuisineId;
    private Integer merchantId;
    private String avatarUrl;
    private String cuisineName;
    private Double price;
    private Double score;
    private String introduce;
}
