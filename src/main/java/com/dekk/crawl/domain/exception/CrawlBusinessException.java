package com.dekk.crawl.domain.exception;

import com.dekk.common.error.BusinessException;
import com.dekk.common.error.ErrorCode;

public class CrawlBusinessException extends BusinessException {

    public CrawlBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
