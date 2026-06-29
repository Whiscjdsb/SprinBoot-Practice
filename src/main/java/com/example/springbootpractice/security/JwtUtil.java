package com.example.springbootpractice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // 密钥（至少 32 个字符）
    private static final String SECRET = "ThisIsASecretKeyForJwtAtLeast32BytesLong!!";
    // 有效期 24 小时
    private static final long EXPIRE_MS = 24 * 60 * 60 * 1000;

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 token
     */
    public static String generateToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRE_MS))
                .signWith(KEY)
                .compact();
    }

    /**
     * 从 token 解析用户名
     */
    public static String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 验证 token 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
