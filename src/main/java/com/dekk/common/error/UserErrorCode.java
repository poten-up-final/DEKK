package com.dekk.common.error;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 400 BAD_REQUEST
    ALREADY_ONBOARDED(HttpStatus.BAD_REQUEST, "EU40001", "이미 온보딩이 완료된 사용자입니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "EU40002", "유효하지 않은 사용자 ID입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "EU40003", "닉네임은 1자 이상 20자 이내여야 합니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU40401", "사용자를 찾을 수 없습니다."),

    // 409 CONFLICT
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "EU40901", "이미 사용 중인 닉네임입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

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
