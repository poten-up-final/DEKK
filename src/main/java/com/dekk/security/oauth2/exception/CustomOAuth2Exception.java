package com.dekk.security.oauth2.exception;

import com.dekk.common.error.ErrorCode;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class CustomOAuth2Exception extends OAuth2AuthenticationException {

    private final ErrorCode errorCode;

    public CustomOAuth2Exception(ErrorCode errorCode) {
        super(new OAuth2Error(errorCode.code(), errorCode.message(), null), errorCode.message());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
