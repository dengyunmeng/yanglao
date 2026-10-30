package com.elderlycare.controller;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.FraudReportDTO;
import com.elderlycare.dto.FraudTestSubmitDTO;
import com.elderlycare.entity.FraudCase;
import com.elderlycare.entity.FraudReport;
import com.elderlycare.service.FraudService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/fraud")
public class FraudController {

    @Resource
    private FraudService fraudService;

    @GetMapping("/cases")
    public Result<List<FraudCase>> cases() {
        return fraudService.cases();
    }

    @PostMapping("/reports")
    public Result<Long> report(@RequestBody FraudReportDTO dto) {
        return fraudService.report(getUserId(), dto);
    }

    @GetMapping("/reports")
    public Result<PageResult<FraudReport>> reports(@RequestParam(defaultValue = "0") Integer isAdmin,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return fraudService.reports(getUserId(), isAdmin, page, pageSize);
    }

    @PostMapping("/tests/submit")
    public Result<Integer> submitTest(@RequestBody FraudTestSubmitDTO dto) {
        return fraudService.submitTest(getUserId(), dto);
    }

    @GetMapping("/tests/history")
    public Result<?> testHistory() {
        return fraudService.testHistory(getUserId());
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
