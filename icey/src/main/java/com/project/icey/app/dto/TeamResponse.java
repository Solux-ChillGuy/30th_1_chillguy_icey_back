package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamResponse {
    private Long id;
    private String teamName;
    private Integer memberNum;
    private String invitation;
}
