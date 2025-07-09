package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LetterSummaryResponse {
    Long id;
    String senderName;
    Boolean isRead;
}
