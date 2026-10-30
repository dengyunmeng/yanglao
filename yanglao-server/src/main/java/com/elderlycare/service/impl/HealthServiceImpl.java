package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderlycare.common.PageQuery;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.HealthCheckinDTO;
import com.elderlycare.entity.HealthRecord;
import com.elderlycare.entity.HealthTip;
import com.elderlycare.mapper.HealthRecordMapper;
import com.elderlycare.mapper.HealthTipMapper;
import com.elderlycare.service.HealthService;
import com.elderlycare.vo.HealthStatsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HealthServiceImpl implements HealthService {

    @Resource
    private HealthRecordMapper healthRecordMapper;

    @Resource
    private HealthTipMapper healthTipMapper;

    @Override
    public Result<Long> submit(Long userId, HealthCheckinDTO dto) {
        HealthRecord record = new HealthRecord();
        record.setUserId(userId);
        record.setSystolicPressure(dto.getSystolicPressure());
        record.setDiastolicPressure(dto.getDiastolicPressure());
        record.setBloodSugar(dto.getBloodSugar());
        record.setSleepHours(dto.getSleepHours());
        record.setHeartRate(dto.getHeartRate());
        record.setFeeling(dto.getFeeling());
        record.setNote(dto.getNote());
        record.setCheckinTime(LocalDateTime.now());
        healthRecordMapper.insert(record);
        return Result.success(record.getId());
    }

    @Override
    public Result<PageResult<HealthRecord>> page(Long userId, Integer page, Integer pageSize,
                                                 String startDate, String endDate) {
        QueryWrapper<HealthRecord> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("checkin_time");
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge("checkin_time", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le("checkin_time", endDate + " 23:59:59");
        }
        Page<HealthRecord> p = new Page<>(page, pageSize);
        Page<HealthRecord> result = healthRecordMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize));
    }

    @Override
    public Result<HealthStatsVO> stats(Long userId) {
        QueryWrapper<HealthRecord> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("checkin_time");
        List<HealthRecord> records = healthRecordMapper.selectList(qw);

        HealthStatsVO vo = new HealthStatsVO();
        vo.setTotalRecords((long) records.size());
        vo.setLastCheckin(records.isEmpty() ? null : records.get(0).getCheckinTime());

        if (!records.isEmpty()) {
            vo.setAvgSystolic(avgIntToBigDecimal(records.stream().map(HealthRecord::getSystolicPressure).toList()));
            vo.setAvgDiastolic(avgIntToBigDecimal(records.stream().map(HealthRecord::getDiastolicPressure).toList()));
            vo.setAvgBloodSugar(avgBigDecimal(records.stream().map(HealthRecord::getBloodSugar).toList()));
            vo.setAvgSleep(avgBigDecimal(records.stream().map(HealthRecord::getSleepHours).toList()));
        }
        return Result.success(vo);
    }

    private BigDecimal avgIntToBigDecimal(List<Integer> list) {
        List<Integer> filtered = list.stream().filter(v -> v != null).toList();
        if (filtered.isEmpty()) return BigDecimal.ZERO;
        long sum = filtered.stream().mapToLong(Integer::longValue).sum();
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(filtered.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal avgBigDecimal(List<BigDecimal> list) {
        List<BigDecimal> filtered = list.stream().filter(v -> v != null).toList();
        if (filtered.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal v : filtered) sum = sum.add(v);
        return sum.divide(BigDecimal.valueOf(filtered.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Result<PageResult<?>> tipsPage(PageQuery query) {
        Page<HealthTip> p = new Page<>(query.getPage(), query.getPageSize());
        Page<HealthTip> result = healthTipMapper.selectPage(p, new QueryWrapper<HealthTip>().orderByDesc("create_time"));
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), query.getPage(), query.getPageSize()));
    }

    @Override
    public Result<Long> checkinDays(Long userId) {
        QueryWrapper<HealthRecord> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        Long total = healthRecordMapper.selectCount(qw);
        return Result.success(total == null ? 0L : total);
    }
}
