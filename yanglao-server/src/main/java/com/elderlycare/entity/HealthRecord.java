package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private BigDecimal bloodSugar;

    private BigDecimal sleepHours;

    private Integer heartRate;

    private String feeling;

    private String note;

    private LocalDateTime checkinTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
