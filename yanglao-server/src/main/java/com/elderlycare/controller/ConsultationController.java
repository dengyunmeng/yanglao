package com.elderlycare.controller;

import com.elderlycare.common.Result;
import com.elderlycare.dto.ConsultationAnswerDTO;
import com.elderlycare.dto.ConsultationCreateDTO;
import com.elderlycare.entity.Consultation;
import com.elderlycare.service.ConsultationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 健康咨询（问医生）：普通用户提问，健康管理员/系统管理员回复。
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Resource
    private ConsultationService consultationService;

    @PostMapping("")
    public Result<Long> ask(@RequestBody ConsultationCreateDTO dto) {
        return consultationService.ask(getUserId(), dto);
    }

    @GetMapping("/my")
    public Result<List<Consultation>> my() {
        return consultationService.mine(getUserId());
    }

    @GetMapping("/answered")
    public Result<List<Consultation>> answered() {
        return consultationService.answered();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<List<Consultation>> pending() {
        return consultationService.pending();
    }

    @PutMapping("/{id}/answer")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<Void> answer(@PathVariable Long id, @RequestBody ConsultationAnswerDTO dto) {
        return consultationService.answer(getUserId(), id, dto);
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
