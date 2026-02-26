package com.dekk.deck.presentation.response;

import com.dekk.common.response.ResultCode;
import org.springframework.http.HttpStatus;

/**
 * Deck 도메인 전용 성공 응답 코드 정의
 */
public enum DeckResultCode implements ResultCode {
    DEFAULT_DECK_CREATE_SUCCESS(HttpStatus.OK, "SD20001", "기본 보관함 생성 성공"),
    CARD_SAVE_SUCCESS(HttpStatus.OK, "SD20002", "보관함 카드 저장 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    DeckResultCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override public HttpStatus status() { return status; }
    @Override public String code() { return code; }
    @Override public String message() { return message; }
}
