package com.elderlycare.controller;

import com.elderlycare.common.PageQuery;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.HealthCheckinDTO;
import com.elderlycare.entity.HealthRecord;
import com.elderlycare.security.JwtAuthenticationFilter;
import com.elderlycare.service.HealthService;
import com.elderlycare.vo.HealthStatsVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Resource
    private HealthService healthService;

    @PostMapping("/records")
    public Result<Long> submit(@RequestBody HealthCheckinDTO dto) {
        return healthService.submit(getUserId(), dto);
    }

    @GetMapping("/records")
    public Result<PageResult<HealthRecord>> page(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        return healthService.page(getUserId(), page, pageSize, startDate, endDate);
    }

    @GetMapping("/records/stats")
    public Result<HealthStatsVO> stats() {
        return healthService.stats(getUserId());
    }

    @GetMapping("/records/checkinDays")
    public Result<Long> checkinDays() {
        return healthService.checkinDays(getUserId());
    }

    @GetMapping("/tips")
    public Result<PageResult<?>> tips(PageQuery query) {
        return healthService.tipsPage(query);
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
