package com.elderlycare.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FraudReportDTO {
    private String name;
    private String phone;
    private String type;
    private String platform;
    private BigDecimal amount;
    private String detail;
}
