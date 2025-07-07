package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteInfoResponse {
    private CardInfo senderCard;
    private CardInfo receiverCard;

    @Getter
    @AllArgsConstructor
    public static class CardInfo {
        private Long id;
        private String nickname;
    }
}
