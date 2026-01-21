package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.service.IPService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ip")
@RequiredArgsConstructor
public class IPController {

    private final IPService ipService;

    @PostMapping
    public void uploadIP(@RequestParam("ip") String ip, @UserId Integer userId) {
        ipService.uploadIP(ip, userId);
    }

}
