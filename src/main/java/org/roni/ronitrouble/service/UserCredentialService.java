package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.dto.userCredential.req.LoginReq;
import org.roni.ronitrouble.dto.userCredential.req.RegisterReq;
import org.roni.ronitrouble.dto.userCredential.resp.LoginResp;
import org.roni.ronitrouble.entity.UserCredential;
import org.roni.ronitrouble.exception.BusinessException;
import org.roni.ronitrouble.exception.exceptions.AuthError;
import org.roni.ronitrouble.exception.exceptions.OtherError;
import org.roni.ronitrouble.mapper.UserCredentialMapper;
import org.roni.ronitrouble.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserCredentialService extends ServiceImpl<UserCredentialMapper, UserCredential> {

    private final PasswordEncoder passwordEncoder;
    private final JwtCache jwtCache;

    public LoginResp login(LoginReq loginReq) {
        UserCredential userCredential = getOneOpt(
                new LambdaQueryWrapper<UserCredential>().eq(UserCredential::getEmail, loginReq.getEmail()))
                .orElseThrow(() -> new BusinessException(AuthError.ACCOUNT_NOT_FOUND_ERROR));
        if (!passwordEncoder.matches(loginReq.getPassword(), userCredential.getPassword())) {
            throw new BusinessException(AuthError.ACCOUNT_NOT_FOUND_ERROR);
        }
        JwtUtil.UserCredentialInfo userCredentialInfo = JwtUtil.UserCredentialInfo.builder()
                .userId(userCredential.getUserId())
                .email(userCredential.getEmail())
                .build();
        var token = JwtUtil.generateToken(userCredentialInfo);

        try {
            if (loginReq.isKeepSignedInFor7Days()) {
                jwtCache.setUserCredentialInfo(token, userCredentialInfo, 7, TimeUnit.DAYS);
            } else {
                jwtCache.setUserCredentialInfo(token, userCredentialInfo, 1, TimeUnit.DAYS);
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException(OtherError.NETWORK_ERROR);
        }

        return new LoginResp(token, userCredential.getEmail());
    }

    public void register(RegisterReq registerReq) {
        var result = list(new LambdaQueryWrapper<UserCredential>()
                .eq(UserCredential::getEmail, registerReq.getEmail()));
        if (!result.isEmpty()) {
            throw new RuntimeException("邮箱已存在！");
        }
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(registerReq.getEmail());
        userCredential.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        save(userCredential);
    }

    public Integer getCurrentMaxMidByUserId(Integer userId) {
        return getOneOpt(new LambdaQueryWrapper<UserCredential>()
                .select(UserCredential::getCurrentMaxMid)
                .eq(UserCredential::getUserId, userId)).orElseThrow().getCurrentMaxMid();
    }

    public void addCurrentMixMidByUserId(Integer currentMaxMid, Integer userId) {
        update(new LambdaUpdateWrapper<UserCredential>()
                .eq(UserCredential::getUserId, userId)
                .set(UserCredential::getCurrentMaxMid, currentMaxMid + 1));
    }

}
