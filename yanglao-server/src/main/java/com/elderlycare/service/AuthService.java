package com.elderlycare.service;

import com.elderlycare.common.Result;
import com.elderlycare.dto.AuthLoginDTO;
import com.elderlycare.dto.AuthRegisterDTO;
import com.elderlycare.dto.AuthResetPasswordDTO;
import com.elderlycare.vo.LoginVO;
import com.elderlycare.vo.UserVO;

public interface AuthService {
    Result<LoginVO> login(AuthLoginDTO dto);

    Result<Void> register(AuthRegisterDTO dto);

    /** 忘记密码：通过手机号直接重置 */
    Result<Void> resetPassword(AuthResetPasswordDTO dto);

    Result<Void> logout(Long userId);

    Result<UserVO> getCurrentUser(Long userId);
}
