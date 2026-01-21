package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.view.HomePageResp;
import org.roni.ronitrouble.enums.PostType;
import org.roni.ronitrouble.service.ViewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewController {

    private final ViewService viewService;

    @GetMapping("/homePage")
    public HomePageResp getHomePage(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String postType) {
        PostType type = null;
        if (postType != null && !postType.isEmpty()) {
            type = PostType.valueOf(postType.toUpperCase());
        }
        return viewService.getHomePage(from, pageSize, type);
    }
}
