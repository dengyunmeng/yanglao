package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String type;

    private String tag;

    private String coverUrl;

    private Long teacherId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Integer maxStudents;

    private Integer currentStudents;

    /** approved 已通过 / pending 待审核 / rejected 已驳回 */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
