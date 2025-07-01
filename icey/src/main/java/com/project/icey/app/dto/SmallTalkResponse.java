package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class SmallTalkResponse {

    private final String title;
    private final List<QuestionTip> questionTips;

    public SmallTalkResponse(String title, List<QuestionTip> questionTips) {
        this.title = title;
        this.questionTips = questionTips;
    }

    @Getter
    public static class QuestionTip {
        private String question;
        private String tip;

        // 기본 생성자, getter/setter 필요하면 추가
    }
}
