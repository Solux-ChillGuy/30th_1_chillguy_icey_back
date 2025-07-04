package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTeamJoinResponse {
    private Long teamId;
    private String teamName;
    private Long userId;
    private String username;
    private String joinedAt;
}
