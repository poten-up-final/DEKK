package com.dekk.app.card.domain.exception;

import com.dekk.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CardErrorCode implements ErrorCode {
    PRODUCT_RAW_DATA_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40001", "상품 원본 데이터는 필수 값입니다."),
    PRODUCT_ORIGIN_URL_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40002", "상품 원본 이미지 경로는 필수 값입니다."),
    PRODUCT_NAME_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40003", "상품 이름은 필수값입니다."),
    PRODUCT_EXTERNAL_ID_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40004", "상품 고유 id는 필수값입니다."),
    CARD_RAW_DATA_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40005", "카드 원본 데이터는 필수 값입니다."),
    CARD_ORIGIN_URL_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40006", "카드 원본 이미지 경로는 필수 값입니다."),
    CARD_ORIGIN_ID_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40007", "카드 고유 id는 필수 값입니다."),
    CARD_PLATFORM_IS_REQUIRED_TO_CREATE(HttpStatus.BAD_REQUEST, "EC40008", "카드 플랫폼은 필수 값입니다."),
    CANNOT_CHANGE_STATUS_OF_DELETE_REQUESTED(HttpStatus.BAD_REQUEST, "EC40009", "삭제 요청된 카드의 상태는 변경할 수 없습니다."),
    CARD_ALREADY_IN_CATEGORY(HttpStatus.BAD_REQUEST, "EC40010", "이미 해당 카테고리에 포함된 카드입니다."),
    CARD_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40011", "카드 ID는 필수값입니다."),
    CATEGORY_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40012", "카테고리 ID는 필수값입니다."),
    CARD_DELETE_REASON_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40013", "카드 삭제 사유는 필수값입니다."),
    ADMIN_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40014", "관리자 ID는 필수값입니다."),
    RESOURCE_ID_IS_REQUIRED_FOR_USER_CARD(HttpStatus.BAD_REQUEST, "EC40015", "사용자 카드 생성 시 리소스 ID는 필수값입니다."),
    TARGET_GENDER_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40016", "성별은 필수값입니다."),
    HEIGHT_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40017", "키는 필수값입니다."),
    WEIGHT_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40018", "몸무게는 필수값입니다."),
    RESOURCE_ID_IS_REQUIRED_FOR_USER_PRODUCT(HttpStatus.BAD_REQUEST, "EC40019", "사용자 상품 생성 시 리소스 ID는 필수값입니다."),
    PRODUCT_BRAND_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40020", "상품 브랜드는 필수값입니다."),
    PRODUCT_PRICE_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40021", "상품 가격은 필수값입니다."),
    PRODUCT_URL_IS_REQUIRED(HttpStatus.BAD_REQUEST, "EC40022", "상품 URL은 필수값입니다."),

    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "EC40401", "카드를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EC40402", "존재하지 않는 카테고리가 포함되어 있습니다."),
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
