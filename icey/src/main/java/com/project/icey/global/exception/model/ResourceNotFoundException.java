package com.project.icey.global.exception.model;


import com.project.icey.global.exception.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class ResourceNotFoundException extends CoreApiException {

    private final List<?> data; // 추가 데이터 리스트

    // 기본 생성자 (data 없이)
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
        this.data = List.of();
    }

    // data 포함 생성자
    public ResourceNotFoundException(ErrorCode errorCode, List<?> data) {
        super(errorCode);
        this.data = data;
    }

    // cause 포함 생성자
    public ResourceNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
        this.data = List.of();
    }

    // cause + data 포함 생성자
    public ResourceNotFoundException(ErrorCode errorCode, List<?> data, Throwable cause) {
        super(errorCode, cause);
        this.data = data;
    }
}