package org.roni.ronitrouble.controller;

import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.dto.userCredential.req.LoginReq;
import org.roni.ronitrouble.dto.userCredential.req.RegisterReq;
import org.roni.ronitrouble.dto.userCredential.resp.LoginResp;
import org.roni.ronitrouble.service.UserCredentialService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userCredential")
public class UserCredentialController {

    private final UserCredentialService userCredentialService;

    @PostMapping("/login")
    public LoginResp login(@RequestBody LoginReq loginReq) {
        return userCredentialService.login(loginReq);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterReq registerReq) {
        userCredentialService.register(registerReq);
    }

}
