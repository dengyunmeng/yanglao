package com.elderlycare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    @Resource
    private JwtProperties jwtProperties;

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(Long userId, String phone, List<String> roles) {
        long expireMs = (long) jwtProperties.getExpireHours() * 60 * 60 * 1000;
        Date now = new Date();
        return Jwts.builder()
                .setSubject(phone)
                .claim("userId", userId)
                .claim("phone", phone)
                .claim("roles", roles)
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
