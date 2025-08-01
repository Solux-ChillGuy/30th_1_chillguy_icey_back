package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SwapResponse {
    private Long fromId; // show = true → false 된 SmallTalk ID
    private Long toId;   // show = false → true 된 SmallTalk ID
}
