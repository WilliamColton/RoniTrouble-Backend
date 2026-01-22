package org.roni.ronitrouble.component.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.component.cache.impl.JwtCache;
import org.roni.ronitrouble.entity.UserCredential;
import org.roni.ronitrouble.service.UserCredentialService;
import org.roni.ronitrouble.util.JwtUtil;
import org.roni.ronitrouble.util.UserContextUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtCache jwtCache;
    private final UserCredentialService userCredentialService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtUtil.UserCredentialInfo userCredentialInfo = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (isTheTokenInTheCache(token)) {
                userCredentialInfo = jwtCache.getUserCredentialInfo(token);
            } else if (JwtUtil.validate(token)) {
                userCredentialInfo = JwtUtil.getUserCredentialInfo(token);
            }
            if (userCredentialInfo != null) {
                if (userCredentialService.exists(new LambdaQueryWrapper<UserCredential>()
                        .eq(UserCredential::getUserId, userCredentialInfo.userId()))) {
                    putAuthenticationInTheSecurityContext(userCredentialInfo);
                    UserContextUtil.setCurrentUserId(userCredentialInfo.userId());
                } else {
                    jwtCache.removeUserCredentialInfo(token);
                    throw new RuntimeException("token 对应用户不存在！");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isTheTokenInTheCache(String token) {
        try {
            return jwtCache.getUserCredentialInfo(token) != null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void putAuthenticationInTheSecurityContext(JwtUtil.UserCredentialInfo userCredentialInfo) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userCredentialInfo.userId()
                        , null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
