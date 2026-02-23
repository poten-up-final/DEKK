package com.dekk.card.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class CardBusinessException extends BusinessException {
    public CardBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
