package com.elderlycare.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.FraudReportDTO;
import com.elderlycare.dto.FraudTestSubmitDTO;
import com.elderlycare.entity.FraudCase;
import com.elderlycare.entity.FraudReport;
import com.elderlycare.entity.FraudTestQuestion;
import com.elderlycare.entity.FraudTestRecord;
import com.elderlycare.mapper.FraudCaseMapper;
import com.elderlycare.mapper.FraudReportMapper;
import com.elderlycare.mapper.FraudTestQuestionMapper;
import com.elderlycare.mapper.FraudTestRecordMapper;
import com.elderlycare.service.FraudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FraudServiceImpl implements FraudService {

    @Resource
    private FraudCaseMapper fraudCaseMapper;

    @Resource
    private FraudReportMapper fraudReportMapper;

    @Resource
    private FraudTestQuestionMapper fraudTestQuestionMapper;

    @Resource
    private FraudTestRecordMapper fraudTestRecordMapper;

    @Override
    public Result<List<FraudCase>> cases() {
        List<FraudCase> list = fraudCaseMapper.selectList(new QueryWrapper<FraudCase>().orderByDesc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<Long> report(Long userId, FraudReportDTO dto) {
        FraudReport r = new FraudReport();
        r.setReporterId(userId);
        r.setName(dto.getName());
        r.setPhone(dto.getPhone());
        r.setType(dto.getType());
        r.setPlatform(dto.getPlatform());
        r.setAmount(dto.getAmount());
        r.setDetail(dto.getDetail());
        r.setStatus("pending");
        fraudReportMapper.insert(r);
        return Result.success(r.getId());
    }

    @Override
    public Result<PageResult<FraudReport>> reports(Long userId, Integer isAdmin, Integer page, Integer pageSize) {
        QueryWrapper<FraudReport> qw = new QueryWrapper<>();
        if (isAdmin == null || isAdmin != 1) qw.eq("reporter_id", userId);
        qw.orderByDesc("create_time");
        Page<FraudReport> p = new Page<>(page, pageSize);
        Page<FraudReport> result = fraudReportMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize));
    }

    @Override
    public Result<Integer> submitTest(Long userId, FraudTestSubmitDTO dto) {
        List<FraudTestSubmitDTO.AnswerItem> answers = dto.getAnswers();
        if (answers == null) answers = new ArrayList<>();

        int total = answers.size();
        int correct = 0;
        for (FraudTestSubmitDTO.AnswerItem item : answers) {
            FraudTestQuestion q = fraudTestQuestionMapper.selectById(item.getQuestionId());
            if (q != null && q.getAnswer() != null && q.getAnswer().equalsIgnoreCase(item.getSelected())) {
                correct++;
            }
        }
        int score = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);

        FraudTestRecord record = new FraudTestRecord();
        record.setUserId(userId);
        record.setScore(score);
        record.setAnswers(JSON.toJSONString(answers));
        record.setCreateTime(LocalDateTime.now());
        fraudTestRecordMapper.insert(record);
        return Result.success(score);
    }

    @Override
    public Result<?> testHistory(Long userId) {
        List<FraudTestRecord> list = fraudTestRecordMapper.selectList(
                new QueryWrapper<FraudTestRecord>().eq("user_id", userId).orderByDesc("create_time"));
        return Result.success(list);
    }
}
