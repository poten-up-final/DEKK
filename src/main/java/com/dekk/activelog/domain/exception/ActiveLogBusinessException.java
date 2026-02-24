package com.dekk.activelog.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class ActiveLogBusinessException extends BusinessException {
    public ActiveLogBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
