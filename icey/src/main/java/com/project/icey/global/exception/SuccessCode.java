package com.project.icey.global.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    // 200 OK
    LOGIN_USER_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다"),
    GET_POST_SUCCESS(HttpStatus.OK, "게시글 조회가 완료되었습니다."),
    GET_ALL_POST_SUCCESS(HttpStatus.OK, "사용자가 작성한 전체 게시글 조회를 완료했습니다"),
    UPDATE_POST_SUCCESS(HttpStatus.OK, "게시글 수정이 완료되었습니다."),
    REPORT_SUCCESS(HttpStatus.OK, "신고가 정상적으로 이루어졌습니다."),
    SMS_CERT_MESSAGE_SUCCESS(HttpStatus.OK, "메세지 전송이 완료되었습니다."),
    SMS_VERIFY_SUCCESS(HttpStatus.OK, "본인확인 인증에 성공했습니다"),
    EMAIL_CERT_MESSAGE_SUCCESS(HttpStatus.OK, "이메일 전송이 완료되었습니다."),
    EMAIL_VERIFY_SUCCESS(HttpStatus.OK, "이메일 인증에 성공했습니다"),
    LETTER_WRITE_INFO_SUCCESS(HttpStatus.OK, "쪽지 작성화면 정보를 불러왔습니다."),
    GET_LETTER_SUCCESS(HttpStatus.OK, "쪽지(들)을 불러오는데 성공했습니다"),



    // 201 Created, Delete
    CREATE_POST_SUCCESS(HttpStatus.CREATED, "게시글 생성이 완료되었습니다."),
    CREATE_TEAM_SUCCESS(HttpStatus.CREATED, "팀이 생성되었습니다."),
    LETTER_SEND_SUCCESS(HttpStatus.CREATED, "쪽지를 성공적으로 보냈습니다."),
    DELETE_ATTENDANCE_SUCCESS(HttpStatus.NO_CONTENT, "작업 현장 탈퇴가 완료되었습니다."),
    DELETE_REPORT_SUCCESS(HttpStatus.NO_CONTENT, "신고 내역이 정상적으로 삭제되었습니다. "),
    DELETE_POST_SUCCESS(HttpStatus.NO_CONTENT, "게시글이 성공적으로 삭제 되었습니다. "),

    //Schedule
    CREATED_SCHEDULE(HttpStatus.CREATED, "약속 잡기가 생성되었습니다."),
    CREATED_VOTE(HttpStatus.OK, "투표가 완료되었습니다."),
    GET_MY_VOTE(HttpStatus.OK, "내 투표를 불러오는데 성공했습니다."),
    GET_VOTE_SUMMARY(HttpStatus.OK, "투표 현황을 불러오는데 성공했습니다."),
    GET_COMBINED_VOTE_SUMMARY(HttpStatus.OK, "투표현황과 내 투표를 동시에 불러오는데 성공했습니다."),
    GET_BEST_CANDIDATE(HttpStatus.OK, "과반수 이상이 가능한 시간들을 불러오는데 성공했습니다."),
    CONFIRMED_SCHEDULE(HttpStatus.OK, "약속이 확정되었습니다."),

    //Team -> create은 위에 있음.
    GET_TEAM_LIST_SUCCESS(HttpStatus.OK, "팀 목록 조회가 완료되었습니다."),
    GET_INVITATION_SUCCESS(HttpStatus.OK, "초대 링크 조회가 완료되었습니다."),
    JOIN_TEAM_SUCCESS(HttpStatus.CREATED, "팀 가입이 완료되었습니다."),
    GET_TEAM_DETAIL_SUCCESS(HttpStatus.OK, "팀 상세 정보 조회가 완료되었습니다."),
    DELETE_TEAM_SUCCESS(HttpStatus.NO_CONTENT, "팀이 성공적으로 삭제되었습니다."),
    LEAVE_TEAM_SUCCESS(HttpStatus.NO_CONTENT, "팀 탈퇴가 완료되었습니다."),
    GET_TEAM_INFO_BY_INVITATION_SUCCESS(HttpStatus.OK, "초대 링크를 통해 팀 정보를 불러왔습니다."),

    // Notice
    NOTICE_CREATED(HttpStatus.CREATED, "공지사항이 성공적으로 생성되었습니다."),
    NOTICE_UPDATED(HttpStatus.OK, "공지사항이 성공적으로 수정되었습니다."),
    NOTICE_DELETED(HttpStatus.OK, "공지사항이 성공적으로 삭제되었습니다."),
    NOTICE_FOUND(HttpStatus.OK, "공지사항 조회 성공"),

    // Recipe
    RECIPE_CREATED(HttpStatus.CREATED, "레시피가 성공적으로 생성되었습니다."),
    RECIPE_UPDATED(HttpStatus.OK, "레시피가 성공적으로 수정되었습니다."),
    RECIPE_DELETED(HttpStatus.OK, "레시피가 성공적으로 삭제되었습니다."),
    RECIPE_FOUND(HttpStatus.OK, "레시피 조회 성공"),

    // Server
    ANALYSIS_SUCCESS(HttpStatus.OK, "이미지 분석 성공"),

    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "이미지 업로드 성공"),

    REWARD_CREATED(HttpStatus.CREATED, "리워드 요청에 성공하였습니다."),
    REWARD_LIST_RETRIEVED(HttpStatus.OK, "리워드 리스트 조회 성공"),
    REWARD_DETAIL_RETRIEVED(HttpStatus.OK, "리워드 디테일 조회 성공"),
    REWARD_STATUS_UPDATED(HttpStatus.OK, "리워드 수정 성공"),

    HOME_DATA_RETRIEVED(HttpStatus.OK, "홈화면으로 정보 받아오기 성공");


    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}
