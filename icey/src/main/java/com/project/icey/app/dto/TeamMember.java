package com.project.icey.app.dto;

import com.project.icey.app.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMember {
    private Long userId;
    private String username;
    private UserRole role;
}
