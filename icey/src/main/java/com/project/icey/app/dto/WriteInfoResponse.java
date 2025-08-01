package com.project.icey.app.dto;

import com.project.icey.app.domain.AccessoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteInfoResponse {
    private CardInfo senderCard;
    private ReceiverCardInfo receiverCard;

    @Getter
    @AllArgsConstructor
    public static class CardInfo {
        private Long cardId;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class ReceiverCardInfo {
        private Long cardId;
        private String nickname;
        private String animal;
        private String profileColor;
        private AccessoryType accessory;
        private String mbti;
        private String hobby;
        private String secretTip;
        private String tmi;
        private String teamName; // ✅ 여기 추가
    }

}
