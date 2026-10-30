package com.elderlycare.service;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.FraudReportDTO;
import com.elderlycare.dto.FraudTestSubmitDTO;
import com.elderlycare.entity.FraudCase;
import com.elderlycare.entity.FraudReport;

import java.util.List;

public interface FraudService {
    Result<List<FraudCase>> cases();

    Result<Long> report(Long userId, FraudReportDTO dto);

    Result<PageResult<FraudReport>> reports(Long userId, Integer isAdmin, Integer page, Integer pageSize);

    Result<Integer> submitTest(Long userId, FraudTestSubmitDTO dto);

    Result<?> testHistory(Long userId);
}
