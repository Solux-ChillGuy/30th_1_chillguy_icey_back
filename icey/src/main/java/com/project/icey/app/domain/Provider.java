package com.project.icey.app.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    KAKAO("Kakao"),
    GOOGLE("Google");

    private final String key;
}
