package com.elderlycare.dto;

import lombok.Data;

@Data
public class AuthResetPasswordDTO {
    private String phone;
    private String newPassword;
}
