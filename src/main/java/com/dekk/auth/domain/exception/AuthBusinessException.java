package com.dekk.auth.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class AuthBusinessException extends BusinessException {
    public AuthBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
