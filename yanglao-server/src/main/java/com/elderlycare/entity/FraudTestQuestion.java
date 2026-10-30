package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fraud_test_question")
public class FraudTestQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String question;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String answer;

    private String analysis;

    private String category;
}
