package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationTeamInfoResponse {
    private final String teamName;
    private final String leaderName;
}
