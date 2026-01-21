package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.dto.userCredential.req.LoginReq;
import org.roni.ronitrouble.dto.userCredential.req.MerchantRegisterReq;
import org.roni.ronitrouble.dto.userCredential.req.RegisterReq;
import org.roni.ronitrouble.dto.userCredential.req.StudentRegisterReq;
import org.roni.ronitrouble.dto.userCredential.resp.LoginResp;
import org.roni.ronitrouble.entity.UserCredential;
import org.roni.ronitrouble.enums.Role;
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
    private final MerchantService merchantService;

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

        return new LoginResp(token, userCredential.getEmail(), userCredential.getRole());
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

    public void registerStudent(StudentRegisterReq registerReq) {
        var result = list(new LambdaQueryWrapper<UserCredential>()
                .eq(UserCredential::getEmail, registerReq.getEmail()));
        if (!result.isEmpty()) {
            throw new RuntimeException("邮箱已存在！");
        }
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(registerReq.getEmail());
        userCredential.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        userCredential.setRole(Role.STUDENT);
        save(userCredential);
    }

    public void registerMerchant(MerchantRegisterReq registerReq) {
        var result = list(new LambdaQueryWrapper<UserCredential>()
                .eq(UserCredential::getEmail, registerReq.getEmail()));
        if (!result.isEmpty()) {
            throw new RuntimeException("邮箱已存在！");
        }
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(registerReq.getEmail());
        userCredential.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        userCredential.setRole(Role.MERCHANT);
        save(userCredential);

        Merchant merchant = new Merchant();
        merchant.setMerchantId(userCredential.getUserId());
        merchant.setName(registerReq.getName());
        merchant.setAddress(registerReq.getAddress());
        merchant.setPhoneNumber(registerReq.getPhoneNumber());
        merchantService.save(merchant);
    }

}
