package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderlycare.common.BusinessException;
import com.elderlycare.common.Result;
import com.elderlycare.dto.ConsultationAnswerDTO;
import com.elderlycare.dto.ConsultationCreateDTO;
import com.elderlycare.entity.Consultation;
import com.elderlycare.entity.SysUser;
import com.elderlycare.mapper.ConsultationMapper;
import com.elderlycare.mapper.SysUserMapper;
import com.elderlycare.service.ConsultationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    @Resource
    private ConsultationMapper consultationMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Result<Long> ask(Long userId, ConsultationCreateDTO dto) {
        if (dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            throw new BusinessException(400, "请输入要咨询的问题");
        }

        Consultation c = new Consultation();
        c.setUserId(userId);
        c.setQuestion(dto.getQuestion().trim());
        SysUser u = sysUserMapper.selectById(userId);
        c.setUserName(u == null ? "匿名用户" : u.getName());
        c.setStatus("pending");
        consultationMapper.insert(c);
        return Result.success(c.getId());
    }

    @Override
    public Result<List<Consultation>> mine(Long userId) {
        List<Consultation> list = consultationMapper.selectList(
                new QueryWrapper<Consultation>().eq("user_id", userId).orderByDesc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<List<Consultation>> pending() {
        List<Consultation> list = consultationMapper.selectList(
                new QueryWrapper<Consultation>().eq("status", "pending").orderByAsc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<Void> answer(Long doctorId, Long id, ConsultationAnswerDTO dto) {
        if (dto.getAnswer() == null || dto.getAnswer().trim().isEmpty()) {
            throw new BusinessException(400, "请填写回复内容");
        }
        Consultation c = consultationMapper.selectById(id);
        if (c == null) throw new BusinessException(404, "提问不存在");

        SysUser doctor = sysUserMapper.selectById(doctorId);
        c.setAnswer(dto.getAnswer().trim());
        c.setDoctorId(doctorId);
        c.setDoctorName(doctor == null ? "医生" : doctor.getName());
        c.setStatus("answered");
        consultationMapper.updateById(c);
        return Result.success();
    }

    @Override
    public Result<List<Consultation>> answered() {
        List<Consultation> list = consultationMapper.selectList(
                new QueryWrapper<Consultation>().eq("status", "answered")
                        .orderByDesc("update_time").last("LIMIT 50"));
        return Result.success(list);
    }
}
