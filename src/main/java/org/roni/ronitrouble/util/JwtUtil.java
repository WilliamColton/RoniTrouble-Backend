package org.roni.ronitrouble.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Builder;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 小时

    private JwtUtil() {
    }

    public static String generateToken(UserCredentialInfo userCredentialInfo) {
        return Jwts.builder()
                .subject(String.valueOf(userCredentialInfo.userId()))
                .claim("email", userCredentialInfo.email())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public static UserCredentialInfo getUserCredentialInfo(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
        return UserCredentialInfo.builder()
                .userId(Integer.valueOf(claims.getSubject()))
                .email(claims.get("email", String.class))
                .build();
    }

    public static boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Builder
    public record UserCredentialInfo(Integer userId, String email) {
    }
    
}
