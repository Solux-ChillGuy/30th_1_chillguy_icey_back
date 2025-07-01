package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallTalkCreateRequest {
    private String target;  // 스몰톡 대상
    private String purpose; // 스몰톡 목적
}
