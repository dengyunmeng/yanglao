package com.elderlycare.service;

import com.elderlycare.common.PageQuery;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.HealthCheckinDTO;
import com.elderlycare.entity.HealthRecord;
import com.elderlycare.vo.HealthStatsVO;

public interface HealthService {
    Result<Long> submit(Long userId, HealthCheckinDTO dto);

    Result<PageResult<HealthRecord>> page(Long userId, Integer page, Integer pageSize,
                                          String startDate, String endDate);

    Result<HealthStatsVO> stats(Long userId);

    Result<PageResult<?>> tipsPage(PageQuery query);

    Result<Long> checkinDays(Long userId);
}
