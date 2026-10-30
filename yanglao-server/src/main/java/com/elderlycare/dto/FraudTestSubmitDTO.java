package com.elderlycare.dto;

import lombok.Data;

import java.util.List;

@Data
public class FraudTestSubmitDTO {
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String selected;
    }
}
