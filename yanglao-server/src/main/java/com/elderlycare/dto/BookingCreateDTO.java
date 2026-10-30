package com.elderlycare.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingCreateDTO {
    private Long courseId;
    private String userName;
    private String userPhone;
    private LocalDate bookDate;
    private String timeSlot;
    private String note;
}
