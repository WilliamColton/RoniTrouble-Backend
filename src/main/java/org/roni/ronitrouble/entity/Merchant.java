package org.roni.ronitrouble.entity;

import lombok.Data;
import org.roni.ronitrouble.enums.MerchantCategory;

import java.time.LocalDateTime;

@Data
public class Merchant {
    private Integer merchantId;//userid
    private String name;
    private String address;
    private String phoneNumber;
    private String avatarUrl;
    private String bannerUrl;
    private Double score;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private MerchantCategory category;
}