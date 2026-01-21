package org.roni.ronitrouble.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder buildBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
        // 通过对关键代码的分析发现强度默认是 10
    }

}
