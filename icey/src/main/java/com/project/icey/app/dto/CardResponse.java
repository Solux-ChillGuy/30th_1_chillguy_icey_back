package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CardResponse {
    private Long cardId; // 카드 ID. 템플릿도 유저도 아님
    private String nickname;
    private String animal;
    private String profileColor;
    private String accessory;
    private String mbti;
    private String hobby;
    private String secretTip;
    private String tmi;
}