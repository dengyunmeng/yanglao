package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation")
public class Consultation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String userName;

    private String question;

    private String answer;

    private Long doctorId;

    private String doctorName;

    /** pending 待回复 / answered 已回复 */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
