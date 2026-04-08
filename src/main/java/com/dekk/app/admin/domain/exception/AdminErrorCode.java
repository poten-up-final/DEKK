package com.dekk.app.admin.domain.exception;

import com.dekk.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    INVALID_ADMIN_DATA(HttpStatus.BAD_REQUEST, "EAD40001", "관리자 필수 정보가 누락되었거나 올바르지 않습니다."),
    INVALID_INSPECTION_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "EAD40002", "올바르지 않은 검수 상태 변경입니다."),
    INVALID_TARGET_INSPECTION_STATUS(
            HttpStatus.BAD_REQUEST, "EAD40003", "관리자는 승인(ADMIN_APPROVED) 또는 반려(ADMIN_REJECTED) 상태로만 변경할 수 있습니다."),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "EAD40101", "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40102", "유효하지 않은 관리자 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40103", "만료된 관리자 토큰입니다."),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40104", "로그아웃 처리되어 무효화된 토큰입니다."),

    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN, "EAD40301", "권한이 없습니다."),

    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "EAD40401", "관리자를 찾을 수 없습니다."),
    INSPECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "EAD40402", "해당 검수 내역을 찾을 수 없습니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "EAD40901", "이미 등록된 이메일입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus status() {
        return status;
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
