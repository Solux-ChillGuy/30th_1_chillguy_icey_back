package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteInfoResponse {
    private CardInfo senderCard;
    private CardResponse receiverCard;

    @Getter
    @AllArgsConstructor
    public static class CardInfo {
        private Long cardId;
        private String nickname;
    }

}
