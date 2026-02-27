package com.dekk.activelog.presentation.response;

import com.dekk.common.response.ResultCode;
import org.springframework.http.HttpStatus;

public enum ActiveLogResultCode implements ResultCode {
    SWIPE_SUCCESS(HttpStatus.OK, "SAL20001", "스와이프 평가가 정상적으로 기록되었습니다."),
    GUEST_SWIPE_IGNORED(HttpStatus.OK, "SAL20002", "비회원 스와이프 액션 (수집 생략)");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ActiveLogResultCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override public HttpStatus status() { return status; }
    @Override public String code() { return code; }
    @Override public String message() { return message; }
}
