package com.elderlycare.controller;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.CourseCreateDTO;
import com.elderlycare.dto.CourseQueryDTO;
import com.elderlycare.entity.Course;
import com.elderlycare.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("")
    public Result<PageResult<Course>> list(CourseQueryDTO query) {
        return courseService.page(query);
    }

    @GetMapping("/my")
    public Result<List<Course>> my() {
        return courseService.myCourses(getUserId());
    }

    /** 健康管理员 / 系统管理员发布课程，需系统管理员审核 */
    @PostMapping("")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<Course> create(@RequestBody CourseCreateDTO dto) {
        return courseService.createCourse(getUserId(), dto);
    }

    /** 我发布的课程（含待审核/已驳回） */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<List<Course>> mine() {
        return courseService.myManaged(getUserId());
    }

    @PostMapping("/my")
    public Result<Void> join(@RequestBody Map<String, Long> body) {
        return courseService.join(getUserId(), body.get("courseId"));
    }

    @DeleteMapping("/my/{courseId}")
    public Result<Void> cancel(@PathVariable Long courseId) {
        return courseService.cancelMyCourse(getUserId(), courseId);
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
