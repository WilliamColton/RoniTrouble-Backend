package org.roni.ronitrouble.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.entity.UserCredential;
import org.roni.ronitrouble.mapper.UserCredentialMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserAuthService extends ServiceImpl<UserCredentialMapper, UserCredential> {

    private final PasswordEncoder passwordEncoder;
    private final JwtCache jwtCache;
    private final UserInfoService userInfoService;

//    public void login(LoginReq loginReq) {
//        Optional<UserAuth> userCredential = getOneOpt(new LambdaQueryWrapper<UserAuth>()
//                .eq(UserAuth::getEmail,loginReq.getEmail())
//                .eq(UserAuth::getPassword, loginReq.getPassword()));
//        if(userCredential.isEmpty()){
//            throw new RuntimeException("账号或密码错误!");
//        }
//    }
//
//    public void register(RegisterReq registerReq) {
//        Optional<UserAuth> userCredential = getOneOpt(new LambdaQueryWrapper<UserAuth>()
//                .eq(UserAuth::getEmail,registerReq.getEmail()));
//        if(userCredential.isPresent()){
//            throw new RuntimeException("账号已存在！请重新输入");
//        }
//        UserAuth user = new UserAuth();
//        user.setEmail(registerReq.getEmail());
//        user.setPassword(registerReq.getPassword());
//        saveOrUpdate(user);
//    }

}

