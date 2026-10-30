package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderlycare.common.BusinessException;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.CourseCreateDTO;
import com.elderlycare.dto.CourseQueryDTO;
import com.elderlycare.entity.Course;
import com.elderlycare.entity.MyCourse;
import com.elderlycare.mapper.CourseMapper;
import com.elderlycare.mapper.MyCourseMapper;
import com.elderlycare.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private MyCourseMapper myCourseMapper;

    @Override
    public Result<PageResult<Course>> page(CourseQueryDTO query) {
        QueryWrapper<Course> qw = new QueryWrapper<>();
        if (query.getType() != null && !query.getType().isEmpty()) qw.eq("type", query.getType());
        if (query.getTag() != null && !query.getTag().isEmpty()) qw.like("tag", query.getTag());
        // 学员只能看到审核通过的课程
        qw.eq("status", "approved");
        qw.orderByDesc("create_time");
        Page<Course> p = new Page<>(query.getPage(), query.getPageSize());
        Page<Course> result = courseMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), query.getPage(), query.getPageSize()));
    }

    @Override
    public Result<List<Course>> myCourses(Long userId) {
        List<MyCourse> mcs = myCourseMapper.selectList(new QueryWrapper<MyCourse>().eq("user_id", userId));
        if (mcs.isEmpty()) return Result.success(new ArrayList<>());
        List<Long> ids = mcs.stream().map(MyCourse::getCourseId).toList();
        List<Course> list = courseMapper.selectBatchIds(ids);
        return Result.success(list);
    }

    @Override
    public Result<Void> join(Long userId, Long courseId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null) throw new BusinessException(404, "课程不存在");
        if (!"approved".equals(c.getStatus())) throw new BusinessException(400, "该课程尚未通过审核");
        QueryWrapper<MyCourse> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("course_id", courseId);
        if (myCourseMapper.selectCount(qw) > 0) throw new BusinessException(400, "已加入该课程");
        MyCourse mc = new MyCourse();
        mc.setUserId(userId);
        mc.setCourseId(courseId);
        mc.setProgress(0);
        mc.setCreateTime(LocalDateTime.now());
        myCourseMapper.insert(mc);

        // 学习人数 +1，让课程卡片的「N人学习」实时更新
        c.setCurrentStudents((c.getCurrentStudents() == null ? 0 : c.getCurrentStudents()) + 1);
        courseMapper.updateById(c);
        return Result.success();
    }

    @Override
    public Result<Void> cancelMyCourse(Long userId, Long courseId) {
        QueryWrapper<MyCourse> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("course_id", courseId);
        myCourseMapper.delete(qw);

        Course c = courseMapper.selectById(courseId);
        if (c != null && c.getCurrentStudents() != null && c.getCurrentStudents() > 0) {
            c.setCurrentStudents(c.getCurrentStudents() - 1);
            courseMapper.updateById(c);
        }
        return Result.success();
    }

    @Override
    public Result<Course> createCourse(Long teacherId, CourseCreateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException(400, "请填写课程名称");
        }
        if (dto.getType() == null || (!"ONLINE".equals(dto.getType()) && !"OFFLINE".equals(dto.getType()))) {
            throw new BusinessException(400, "课程类型只能是 ONLINE 或 OFFLINE");
        }
        if ("OFFLINE".equals(dto.getType())) {
            if (dto.getStartTime() == null) throw new BusinessException(400, "线下课程需填写开课时间");
            if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
                throw new BusinessException(400, "线下课程需填写上课地点");
            }
        }

        Course c = new Course();
        c.setName(dto.getName().trim());
        c.setDescription(dto.getDescription());
        c.setType(dto.getType());
        c.setTag(dto.getTag() == null || dto.getTag().isEmpty() ? "新上" : dto.getTag());
        c.setTeacherId(teacherId);
        c.setStartTime(dto.getStartTime());
        c.setEndTime(dto.getEndTime());
        c.setLocation(dto.getLocation());
        c.setMaxStudents(dto.getMaxStudents() == null ? 30 : dto.getMaxStudents());
        c.setCurrentStudents(0);
        // 发布的课程需要系统管理员审核后才对学员可见
        c.setStatus("pending");
        courseMapper.insert(c);
        return Result.success(c);
    }

    @Override
    public Result<List<Course>> myManaged(Long teacherId) {
        List<Course> list = courseMapper.selectList(new QueryWrapper<Course>()
                .eq("teacher_id", teacherId).orderByDesc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<List<Course>> pendingCourses() {
        List<Course> list = courseMapper.selectList(new QueryWrapper<Course>()
                .eq("status", "pending").orderByAsc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<Void> review(Long id, boolean approved) {
        Course c = courseMapper.selectById(id);
        if (c == null) throw new BusinessException(404, "课程不存在");
        c.setStatus(approved ? "approved" : "rejected");
        courseMapper.updateById(c);
        return Result.success();
    }
}
