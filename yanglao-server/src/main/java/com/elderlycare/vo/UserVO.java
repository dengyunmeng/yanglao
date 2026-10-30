package com.elderlycare.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private Integer status;
    private List<String> roles;
}
