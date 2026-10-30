package com.elderlycare.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HealthCheckinDTO {
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private BigDecimal bloodSugar;
    private BigDecimal sleepHours;
    private Integer heartRate;
    private String feeling;
    private String note;
}
