package com.elderlycare.controller;

import com.elderlycare.common.Result;
import com.elderlycare.dto.ReviewDTO;
import com.elderlycare.entity.Course;
import com.elderlycare.entity.News;
import com.elderlycare.service.CourseService;
import com.elderlycare.service.NewsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统管理员审核台：审核健康管理员发布的课程、审核资讯。
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Resource
    private CourseService courseService;

    @Resource
    private NewsService newsService;

    @GetMapping("/courses/pending")
    public Result<List<Course>> pendingCourses() {
        return courseService.pendingCourses();
    }

    @PutMapping("/courses/{id}/review")
    public Result<Void> reviewCourse(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        return courseService.review(id, Boolean.TRUE.equals(dto.getApproved()));
    }

    @GetMapping("/news/pending")
    public Result<List<News>> pendingNews() {
        return newsService.pending();
    }

    @PutMapping("/news/{id}/review")
    public Result<Void> reviewNews(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        return newsService.review(id, Boolean.TRUE.equals(dto.getApproved()));
    }
}
