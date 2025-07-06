package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationResponse {

    private Long id;
    private String teamName;
    private String invitationLink;
}
