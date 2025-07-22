package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CardResponse {
    private Long cardId; // 카드 ID. 템플릿도 유저도 아님
    private Long templateId;         // 이 카드가 파생된 템플릿 id (템플릿 자체면 == cardId)
    private Long userId;          // 명함 주인
    private String nickname;
    private String animal;
    private String profileColor;
    private String accessory;
    private String mbti;
    private String hobby;
    private String secretTip;
    private String tmi;
}