package com.dekk.app.resource.domain.exception;

import com.dekk.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ResourceErrorCode implements ErrorCode {
    RESOURCE_TYPE_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40001", "리소스 타입은 필수값입니다."),
    ORIGINAL_KEY_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40002", "원본 파일 키는 필수값입니다."),
    ORIGINAL_FILE_NAME_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40003", "원본 파일명은 필수값입니다."),
    EXPIRES_AT_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40004", "만료 시간은 필수값입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ResourceErrorCode(HttpStatus httpStatus, String code, String message) {
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
