package com.elderlycare.controller;

import com.elderlycare.common.Result;
import com.elderlycare.dto.UserChangePasswordDTO;
import com.elderlycare.dto.UserUpdateDTO;
import com.elderlycare.service.UserService;
import com.elderlycare.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PutMapping("")
    public Result<UserVO> update(@RequestBody UserUpdateDTO dto) {
        return userService.updateProfile(getUserId(), dto);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody UserChangePasswordDTO dto) {
        return userService.changePassword(getUserId(), dto);
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
