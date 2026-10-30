package com.elderlycare.service;

import com.elderlycare.common.Result;
import com.elderlycare.dto.ConsultationAnswerDTO;
import com.elderlycare.dto.ConsultationCreateDTO;
import com.elderlycare.entity.Consultation;

import java.util.List;

public interface ConsultationService {
    Result<Long> ask(Long userId, ConsultationCreateDTO dto);

    Result<List<Consultation>> mine(Long userId);

    Result<List<Consultation>> pending();

    Result<Void> answer(Long doctorId, Long id, ConsultationAnswerDTO dto);

    Result<List<Consultation>> answered();
}
