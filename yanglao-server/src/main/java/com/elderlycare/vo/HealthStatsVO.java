package com.elderlycare.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HealthStatsVO {
    private Long totalRecords;
    private BigDecimal avgSystolic;
    private BigDecimal avgDiastolic;
    private BigDecimal avgBloodSugar;
    private BigDecimal avgSleep;
    private LocalDateTime lastCheckin;
}
