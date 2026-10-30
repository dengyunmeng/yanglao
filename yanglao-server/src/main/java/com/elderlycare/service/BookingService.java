package com.elderlycare.service;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.BookingCreateDTO;
import com.elderlycare.vo.BookingVO;

import java.util.List;

public interface BookingService {
    Result<Long> create(Long userId, BookingCreateDTO dto);

    Result<PageResult<BookingVO>> page(Long userId, Integer isAdmin, Integer page, Integer pageSize);

    /** 管理端预约名单：系统管理员看全部，健康管理员只看自己课程的预约 */
    Result<List<BookingVO>> manage(Long userId, boolean isAdmin, Long courseId);

    Result<Void> confirm(Long id);

    Result<Void> cancel(Long userId, boolean isAdmin, Long id);

    Result<Long> count(Long userId);
}
