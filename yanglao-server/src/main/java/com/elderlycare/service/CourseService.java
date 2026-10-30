package com.elderlycare.service;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.CourseCreateDTO;
import com.elderlycare.dto.CourseQueryDTO;
import com.elderlycare.entity.Course;

import java.util.List;

public interface CourseService {
    Result<PageResult<Course>> page(CourseQueryDTO query);

    Result<List<Course>> myCourses(Long userId);

    Result<Void> join(Long userId, Long courseId);

    Result<Void> cancelMyCourse(Long userId, Long courseId);

    /** 健康管理员/系统管理员发布课程（默认待审核） */
    Result<Course> createCourse(Long teacherId, CourseCreateDTO dto);

    /** 我发布的课程（含待审核/已驳回） */
    Result<List<Course>> myManaged(Long teacherId);

    /** 待审核课程（系统管理员） */
    Result<List<Course>> pendingCourses();

    /** 审核课程（系统管理员） */
    Result<Void> review(Long id, boolean approved);
}
