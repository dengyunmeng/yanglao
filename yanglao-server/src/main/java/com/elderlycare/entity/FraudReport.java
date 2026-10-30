package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fraud_report")
public class FraudReport {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    private String name;

    private String phone;

    private String type;

    private String platform;

    private BigDecimal amount;

    private String detail;

    private String status;

    private Long handlerId;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
