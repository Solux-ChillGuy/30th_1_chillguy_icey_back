package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamResponse {
    private Long id;
    private String teamName;
    private String invitation;
    private String dDay;
}
