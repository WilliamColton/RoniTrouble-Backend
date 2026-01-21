package org.roni.ronitrouble.component.cache.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.cache.StringCache;
import org.roni.ronitrouble.util.JwtUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class JwtCache extends StringCache {

    private final ObjectMapper objectMapper;

    public JwtUtil.UserCredentialInfo getUserCredentialInfo(String token) throws JsonProcessingException {
        var value = getValue(token);
        if (value == null || value.isBlank()) {
            return null;
        }
        return objectMapper.readValue(value, JwtUtil.UserCredentialInfo.class);
    }

    public void setUserCredentialInfo(String token, JwtUtil.UserCredentialInfo userCredentialInfo, long timeout, TimeUnit unit) throws JsonProcessingException {
        setValue(token, objectMapper.writeValueAsString(userCredentialInfo), timeout, unit);
    }

}
