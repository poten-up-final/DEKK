package com.dekk.app.admin.domain.exception;

import com.dekk.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    INVALID_ADMIN_DATA(HttpStatus.BAD_REQUEST, "EAD40001", "관리자 필수 정보가 누락되었거나 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "EAD40101", "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40102", "유효하지 않은 관리자 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40103", "만료된 관리자 토큰입니다."),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40104", "로그아웃 처리되어 무효화된 토큰입니다."),
    INVALID_INVITE_TOKEN(HttpStatus.UNAUTHORIZED, "EAD40105", "유효하지 않거나 만료된 초대 토큰입니다."),
    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN, "EAD40301", "권한이 없습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "EAD40302", "로그인 실패 5회 누적으로 30분간 계정이 잠겼습니다."),
    ACCOUNT_SUSPENDED(HttpStatus.FORBIDDEN, "EAD40303", "관리자에 의해 정지된 계정입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "EAD40401", "관리자를 찾을 수 없습니다."),
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
