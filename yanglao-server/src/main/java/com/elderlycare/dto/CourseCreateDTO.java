package com.elderlycare.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseCreateDTO {
    private String name;
    private String description;
    private String type;
    private String tag;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer maxStudents;
}
