package com.elderlycare.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain configured");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> {})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        // 未携带/携带无效 token 访问受保护接口 → 401，前端据此清理会话并要求重新登录
                        .authenticationEntryPoint((req, resp, e) -> {
                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.setContentType("application/json;charset=UTF-8");
                            resp.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                        })
                        // 已登录但权限不足 → 403
                        .accessDeniedHandler((req, resp, e) -> {
                            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            resp.setContentType("application/json;charset=UTF-8");
                            resp.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 课程列表仅 GET 公开，POST/DELETE 等操作必须携带有效 token
                        .antMatchers(HttpMethod.GET, "/api/courses").permitAll()
                        .antMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/reset-password",
                                "/api/public/**",
                                "/api/health/tips",
                                "/api/news/**",
                                "/api/fraud/cases",
                                "/druid/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
