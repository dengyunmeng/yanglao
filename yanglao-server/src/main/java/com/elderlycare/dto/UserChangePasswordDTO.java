package com.elderlycare.dto;

import lombok.Data;

@Data
public class UserChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
