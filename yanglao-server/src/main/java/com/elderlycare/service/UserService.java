package com.elderlycare.service;

import com.elderlycare.common.Result;
import com.elderlycare.dto.UserChangePasswordDTO;
import com.elderlycare.dto.UserUpdateDTO;
import com.elderlycare.vo.UserVO;

public interface UserService {
    Result<UserVO> updateProfile(Long userId, UserUpdateDTO dto);

    Result<Void> changePassword(Long userId, UserChangePasswordDTO dto);
}
