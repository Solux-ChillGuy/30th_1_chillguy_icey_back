package com.project.icey.app.dto;

import com.project.icey.app.domain.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String email;
    private String name;
    private Provider provider;
}

