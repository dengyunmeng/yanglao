package com.elderlycare.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String header = request.getHeader(jwtProperties.getHeader());
        boolean hasToken = StringUtils.hasText(header) && header.startsWith(jwtProperties.getPrefix());
        // 无 token 且路径在白名单内 → 匿名放行；无 token 且不在白名单 → 由 SecurityConfig 拦截为 401
        // 携带 token 时无论是否白名单都执行解析，避免白名单路径下的受保护操作丢失身份
        if (!hasToken) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(jwtProperties.getPrefix().length()).trim();
        try {
            Claims claims = jwtUtil.parse(token);
            if (!jwtUtil.validate(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jti = claims.get("jti", String.class);
            if (jti == null || Boolean.FALSE.equals(redisTemplate.hasKey("token:" + jti))) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = claims.get("userId", Long.class);
            String phone = claims.get("phone", String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = roles == null
                    ? Collections.emptyList()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, phone, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Token validated for userId={}", userId);
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
