package com.dekk.app.resource.domain.exception;

import com.dekk.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ResourceErrorCode implements ErrorCode {
    RESOURCE_TYPE_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40001", "리소스 타입은 필수값입니다."),
    ORIGINAL_KEY_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40002", "원본 파일 키는 필수값입니다."),
    ORIGINAL_FILE_NAME_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40003", "원본 파일명은 필수값입니다."),
    EXPIRES_AT_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40004", "만료 시간은 필수값입니다."),
    ORIGINAL_FILE_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "ER40005", "원본 파일명은 255자를 초과할 수 없습니다."),
    PROCESSED_KEY_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40006", "처리된 파일 키는 필수값입니다."),
    IMAGE_URL_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40007", "이미지 URL은 필수값입니다."),
    FILE_SIZE_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40008", "파일 크기는 필수값입니다."),
    CONTENT_TYPE_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40009", "컨텐츠 타입은 필수값입니다."),
    FILE_EXTENSION_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ER40010", "파일 확장자는 필수입니다."),
    UNSUPPORTED_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "ER40011", "지원하지 않는 파일 확장자입니다."),

    INVALID_STATUS_CHANGE(HttpStatus.CONFLICT, "ER40901", "현재 상태에서는 상태 변경이 불가능합니다.");

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
