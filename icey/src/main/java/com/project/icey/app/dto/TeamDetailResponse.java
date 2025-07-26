package com.project.icey.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamDetailResponse {
    private Long teamId;
    private String teamName;
    private Integer memberCount;
    private String currentDate;
    private String dDay;
    private String role;
    private boolean hasSchedule;
    private String confirmedDate;
    private String createdAt;
    private boolean isAllVoted;
}
