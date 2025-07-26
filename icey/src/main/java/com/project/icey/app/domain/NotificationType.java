package com.project.icey.app.domain;

public enum NotificationType {
    LETTER,             // 쪽지 알림
    TEAM_EXPIRATION_1,  // 팀 유효기간 1일 전
    TEAM_EXPIRATION_3,  // 팀 유효기간 3일 전
    APPOINTMENT_CREATED, // 약속 잡기 생성 알림
    APPOINTMENT_COMPLETED, // 약속 완료 알림
    APPOINTMENT_REGISTRATION_COMPLETED, // 약속 시간 등록 알림
    BALANCE_GAME_CREATED, // 밸런스 게임 생성 알림
    SYSTEM_ALERT    // 시스템 알림
}
