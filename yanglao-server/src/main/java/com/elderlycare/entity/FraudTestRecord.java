package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fraud_test_record")
public class FraudTestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer score;

    private String answers;

    private LocalDateTime createTime;
}
