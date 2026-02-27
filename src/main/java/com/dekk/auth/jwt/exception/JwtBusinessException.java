package com.dekk.auth.jwt.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class JwtBusinessException extends BusinessException {
    public JwtBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
