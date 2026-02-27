package com.dekk.deck.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class DeckBusinessException extends BusinessException {
    public DeckBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
