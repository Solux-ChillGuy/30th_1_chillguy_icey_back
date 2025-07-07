package com.project.icey.app.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LetterSendRequest {
    private Long receiverCardId;
    private String content;
}
