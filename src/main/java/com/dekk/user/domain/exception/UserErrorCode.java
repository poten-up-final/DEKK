package com.dekk.user.domain.exception;

import com.dekk.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    // 400 BAD_REQUEST
    ALREADY_ONBOARDED(HttpStatus.BAD_REQUEST, "EU40001", "이미 온보딩이 완료된 사용자입니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "EU40002", "유효하지 않은 사용자 ID입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "EU40003", "닉네임은 1자 이상 10자 이내여야 하며, 특수문자는 '_'만 허용됩니다."),
    INVALID_PROFILE_INFO(HttpStatus.BAD_REQUEST, "EU40004", "키와 몸무게는 필수 입력값입니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU40401", "사용자를 찾을 수 없습니다."),

    // 409 CONFLICT
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "EU40901", "이미 사용 중인 닉네임입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus status() {
        return httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
