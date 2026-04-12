package com.dekk.app.resource.domain.exception;

import com.dekk.global.error.BusinessException;
import com.dekk.global.error.ErrorCode;

public class ResourceBusinessException extends BusinessException {
    public ResourceBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
