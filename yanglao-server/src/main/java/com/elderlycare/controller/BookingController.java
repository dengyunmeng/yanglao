package com.elderlycare.controller;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.BookingCreateDTO;
import com.elderlycare.service.BookingService;
import com.elderlycare.vo.BookingVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Resource
    private BookingService bookingService;

    @PostMapping("")
    public Result<Long> create(@RequestBody BookingCreateDTO dto) {
        return bookingService.create(getUserId(), dto);
    }

    @GetMapping("")
    public Result<PageResult<BookingVO>> list(@RequestParam(defaultValue = "0") Integer isAdmin,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return bookingService.page(getUserId(), isAdmin, page, pageSize);
    }

    /** 预约名单：健康管理员看自己课程的预约，系统管理员看全部 */
    @GetMapping("/manage")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<List<BookingVO>> manage(@RequestParam(required = false) Long courseId) {
        return bookingService.manage(getUserId(), isAdmin(), courseId);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public Result<Void> confirm(@PathVariable Long id) {
        return bookingService.confirm(id);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        return bookingService.cancel(getUserId(), isAdmin(), id);
    }

    @GetMapping("/count")
    public Result<Long> count() {
        return bookingService.count(getUserId());
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
