package com.project.icey.app.dto;

import com.project.icey.app.domain.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmallTalkDto {
    private Long id;
    private String question;
    private String tip;
    private String answer;
    private QuestionType questionType;

    public SmallTalkDto(){}

    public SmallTalkDto(Long id, String question, String tip, String answer, QuestionType questionType) {
        this.id = id;
        this.question = question;
        this.tip = tip;
        this.answer = answer;
        this.questionType = questionType;
    }


}