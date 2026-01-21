package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.vie.HomePageResp;
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
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return viewService.getHomePage(from, pageSize);
    }
}
