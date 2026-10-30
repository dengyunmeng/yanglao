package com.elderlycare.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    /** true = 审核通过，false = 驳回 */
    private Boolean approved;
}
