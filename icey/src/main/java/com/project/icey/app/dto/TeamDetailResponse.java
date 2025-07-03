package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamDetailResponse {
    private Long teamId;
    private String teamName;
    private Integer memberNum;
    private String dDay;
    private List<TeamMember> members;
}
