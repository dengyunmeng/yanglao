package com.elderlycare.dto;

import com.elderlycare.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseQueryDTO extends PageQuery {
    private String type;
    private String tag;
}
