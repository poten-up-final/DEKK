package com.dekk.app.admin.presentation.response;

import com.dekk.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AdminResultCode implements ResultCode {
    ADMIN_LOGIN_SUCCESS(HttpStatus.OK, "SAD20001", "관리자 로그인에 성공했습니다."),
    ADMIN_LOGOUT_SUCCESS(HttpStatus.OK, "SAD20002", "관리자 로그아웃에 성공했습니다.");

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
