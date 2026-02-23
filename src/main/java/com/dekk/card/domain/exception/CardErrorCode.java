package com.dekk.card.domain.exception;

import com.dekk.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CardErrorCode implements ErrorCode {
    PRODUCT_RAW_DATA_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC001", "상품 원본 데이터는 필수 값입니다"),
    PRODUCT_ORIGIN_URL_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC002", "상품 원본 이미지 경로는 필수 값입니다"),
    PRODUCT_NAME_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC003", "상품 이름은 필수값입니다"),
    PRODUCT_EXTERNAL_ID_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC004", "상품 고유 id는 필수값입니다"),
    CARD_RAW_DATA_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC005", "카드 원본 데이터는 필수 값입니다"),
    CARD_ORIGIN_URL_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC006", "카드 원본 이미지 경로는 필수 값입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CardErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus status() {
        return httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
