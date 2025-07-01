package com.project.icey.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmallTalkDto {
    private Long id;
    private String question;
    private String tip;
    private String answer;

    public SmallTalkDto(){}

    public SmallTalkDto(Long id, String question, String tip, String answer) {
        this.id = id;
        this.question = question;
        this.tip = tip;
        this.answer = answer;
    }


}