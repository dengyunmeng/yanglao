package com.elderlycare.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingVO {
    private Long id;
    private Long courseId;
    private String courseName;
    private String courseType;
    private Long userId;
    private String userName;
    private String userPhone;
    private LocalDate bookDate;
    private String timeSlot;
    private String note;
    private String status;
    private Integer signinStatus;
    private LocalDateTime createTime;
}
