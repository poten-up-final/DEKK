package com.dekk.global.security.oauth2.handler;

import com.dekk.global.error.GlobalErrorCode;
import com.dekk.global.security.oauth2.dto.ErrorQueryParam;
import com.dekk.global.security.oauth2.exception.CustomOAuth2Exception;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ErrorMapper {

    public ErrorQueryParam mapError(AuthenticationException exception) {
        if (exception instanceof CustomOAuth2Exception customException) {
            return ErrorQueryParam.of(customException.getErrorCode().code(), null);
        }

        if (exception instanceof OAuth2AuthenticationException oAuthException) {
            OAuth2Error error = oAuthException.getError();
            return ErrorQueryParam.of(error.getErrorCode(), null);
        }

        return ErrorQueryParam.of(GlobalErrorCode.INTERNAL_ERROR.code(), null);
    }
}
