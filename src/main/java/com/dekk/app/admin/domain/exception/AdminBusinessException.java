package com.dekk.app.admin.domain.exception;

import com.dekk.global.error.BusinessException;
import com.dekk.global.error.ErrorCode;

public class AdminBusinessException extends BusinessException {

    public AdminBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
