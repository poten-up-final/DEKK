package com.dekk.user.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class UserBusinessException extends BusinessException {
    public UserBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
