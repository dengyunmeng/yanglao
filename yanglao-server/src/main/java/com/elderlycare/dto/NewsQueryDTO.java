package com.elderlycare.dto;

import com.elderlycare.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsQueryDTO extends PageQuery {
    private String category;
    private String keyword;
}
