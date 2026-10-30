package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderlycare.common.BusinessException;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.BookingCreateDTO;
import com.elderlycare.entity.Booking;
import com.elderlycare.entity.Course;
import com.elderlycare.entity.SysUser;
import com.elderlycare.mapper.BookingMapper;
import com.elderlycare.mapper.CourseMapper;
import com.elderlycare.mapper.SysUserMapper;
import com.elderlycare.service.BookingService;
import com.elderlycare.vo.BookingVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Result<Long> create(Long userId, BookingCreateDTO dto) {
        if (dto.getCourseId() == null) throw new BusinessException(400, "请选择要预约的课程");
        Course c = courseMapper.selectById(dto.getCourseId());
        if (c == null) throw new BusinessException(404, "课程不存在");
        if (!"OFFLINE".equals(c.getType())) throw new BusinessException(400, "该课程为线上课程，无需预约");
        if (!"approved".equals(c.getStatus())) throw new BusinessException(400, "该课程尚未通过审核，暂不能预约");
        if (dto.getBookDate() == null) throw new BusinessException(400, "请选择预约日期");
        if (dto.getTimeSlot() == null || dto.getTimeSlot().trim().isEmpty()) {
            throw new BusinessException(400, "请选择预约时间段");
        }
        if (dto.getBookDate().isBefore(LocalDate.now())) {
            throw new BusinessException(400, "预约日期不能早于今天");
        }

        int max = c.getMaxStudents() == null ? 30 : c.getMaxStudents();
        int cur = c.getCurrentStudents() == null ? 0 : c.getCurrentStudents();
        if (cur >= max) throw new BusinessException(400, "该课程名额已满，请选择其他课程");

        // 同一用户同一课程同一天同一时段只能预约一次
        QueryWrapper<Booking> dup = new QueryWrapper<>();
        dup.eq("user_id", userId)
                .eq("course_id", dto.getCourseId())
                .eq("book_date", dto.getBookDate())
                .eq("time_slot", dto.getTimeSlot().trim())
                .ne("status", "cancelled");
        if (bookingMapper.selectCount(dup) > 0) {
            throw new BusinessException(400, "您已预约过该时段，请勿重复预约");
        }

        Booking b = new Booking();
        b.setCourseId(dto.getCourseId());
        b.setUserId(userId);

        SysUser u = sysUserMapper.selectById(userId);
        String name = dto.getUserName() == null ? "" : dto.getUserName().trim();
        String phone = dto.getUserPhone() == null ? "" : dto.getUserPhone().trim();
        b.setUserName(name.isEmpty() ? (u == null ? "匿名用户" : u.getName()) : name);
        b.setUserPhone(phone.isEmpty() ? (u == null ? "" : u.getPhone()) : phone);
        b.setBookDate(dto.getBookDate());
        b.setTimeSlot(dto.getTimeSlot().trim());
        b.setNote(dto.getNote());
        b.setStatus("pending");
        b.setSigninStatus(0);
        bookingMapper.insert(b);

        // 更新课程当前人数
        c.setCurrentStudents(cur + 1);
        courseMapper.updateById(c);
        return Result.success(b.getId());
    }

    @Override
    public Result<PageResult<BookingVO>> page(Long userId, Integer isAdmin, Integer page, Integer pageSize) {
        QueryWrapper<Booking> qw = new QueryWrapper<>();
        if (isAdmin == null || isAdmin != 1) {
            qw.eq("user_id", userId);
        }
        qw.orderByDesc("create_time");
        Page<Booking> p = new Page<>(page, pageSize);
        Page<Booking> result = bookingMapper.selectPage(p, qw);
        List<BookingVO> list = toVOList(result.getRecords());
        return Result.success(new PageResult<>(list, result.getTotal(), page, pageSize));
    }

    @Override
    public Result<List<BookingVO>> manage(Long userId, boolean isAdmin, Long courseId) {
        QueryWrapper<Course> cq = new QueryWrapper<>();
        if (!isAdmin) {
            // 健康管理员只能看自己发布课程的预约
            cq.eq("teacher_id", userId);
        }
        List<Course> myCourses = courseMapper.selectList(cq);
        if (myCourses.isEmpty()) return Result.success(new ArrayList<>());

        List<Long> courseIds = new ArrayList<>();
        for (Course c : myCourses) courseIds.add(c.getId());
        if (courseId != null && !courseIds.contains(courseId)) {
            return Result.success(new ArrayList<>());
        }

        QueryWrapper<Booking> qw = new QueryWrapper<>();
        if (courseId != null) {
            qw.eq("course_id", courseId);
        } else {
            qw.in("course_id", courseIds);
        }
        qw.orderByDesc("create_time");
        return Result.success(toVOList(bookingMapper.selectList(qw)));
    }

    @Override
    public Result<Void> confirm(Long id) {
        Booking b = bookingMapper.selectById(id);
        if (b == null) throw new BusinessException(404, "预约不存在");
        b.setStatus("confirmed");
        bookingMapper.updateById(b);
        return Result.success();
    }

    @Override
    public Result<Void> cancel(Long userId, boolean isAdmin, Long id) {
        Booking b = bookingMapper.selectById(id);
        if (b == null) throw new BusinessException(404, "预约不存在");
        if (!isAdmin && !b.getUserId().equals(userId)) throw new BusinessException(403, "无权操作");
        if ("cancelled".equals(b.getStatus())) throw new BusinessException(400, "该预约已取消");

        b.setStatus("cancelled");
        bookingMapper.updateById(b);

        Course c = courseMapper.selectById(b.getCourseId());
        if (c != null) {
            int cur = c.getCurrentStudents() == null ? 0 : c.getCurrentStudents();
            c.setCurrentStudents(Math.max(0, cur - 1));
            courseMapper.updateById(c);
        }
        return Result.success();
    }

    @Override
    public Result<Long> count(Long userId) {
        Long cnt = bookingMapper.selectCount(new QueryWrapper<Booking>()
                .eq("user_id", userId).ne("status", "cancelled"));
        return Result.success(cnt == null ? 0L : cnt);
    }

    /** 预约记录补上课程名称，前端表格可直接展示 */
    private List<BookingVO> toVOList(List<Booking> records) {
        if (records == null || records.isEmpty()) return new ArrayList<>();

        List<Long> courseIds = new ArrayList<>();
        for (Booking b : records) {
            if (b.getCourseId() != null && !courseIds.contains(b.getCourseId())) courseIds.add(b.getCourseId());
        }
        Map<Long, Course> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseMapper.selectBatchIds(courseIds);
            for (Course c : courses) courseMap.put(c.getId(), c);
        }

        List<BookingVO> list = new ArrayList<>();
        for (Booking b : records) {
            BookingVO vo = new BookingVO();
            vo.setId(b.getId());
            vo.setCourseId(b.getCourseId());
            vo.setUserId(b.getUserId());
            vo.setUserName(b.getUserName());
            vo.setUserPhone(b.getUserPhone());
            vo.setBookDate(b.getBookDate());
            vo.setTimeSlot(b.getTimeSlot());
            vo.setNote(b.getNote());
            vo.setStatus(b.getStatus());
            vo.setSigninStatus(b.getSigninStatus());
            vo.setCreateTime(b.getCreateTime());
            Course c = courseMap.get(b.getCourseId());
            if (c != null) {
                vo.setCourseName(c.getName());
                vo.setCourseType(c.getType());
            }
            list.add(vo);
        }
        return list;
    }
}
