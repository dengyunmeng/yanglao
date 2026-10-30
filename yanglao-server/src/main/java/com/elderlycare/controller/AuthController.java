package com.elderlycare.controller;

import com.elderlycare.common.Result;
import com.elderlycare.dto.AuthLoginDTO;
import com.elderlycare.dto.AuthRegisterDTO;
import com.elderlycare.dto.AuthResetPasswordDTO;
import com.elderlycare.security.JwtUtil;
import com.elderlycare.service.AuthService;
import com.elderlycare.vo.LoginVO;
import com.elderlycare.vo.UserVO;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody AuthLoginDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody AuthRegisterDTO dto) {
        return authService.register(dto);
    }

    /** 忘记密码：手机号 + 新密码 直接重置 */
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody AuthResetPasswordDTO dto) {
        return authService.resetPassword(dto);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = getCurrentUserId();
        return authService.logout(userId);
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Long userId = getCurrentUserId();
        return authService.getCurrentUser(userId);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }
}
