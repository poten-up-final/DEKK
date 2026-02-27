package com.dekk.crawl.domain.exception;

import com.dekk.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CrawlErrorCode implements ErrorCode {
    BATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "ECR40401", "배치를 찾을 수 없습니다"),
    BATCH_NOT_COLLECTING(HttpStatus.BAD_REQUEST, "ECR40001", "수집 중인 배치가 아닙니다"),
    BATCH_NOT_COLLECTED(HttpStatus.BAD_REQUEST, "ECR40002", "수집 완료된 배치가 아닙니다"),
    BATCH_NOT_PROCESSING(HttpStatus.BAD_REQUEST, "ECR40005", "정제 처리 중인 배치가 아닙니다"),
    RAW_DATA_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ECR50001", "원본 데이터 파싱에 실패했습니다"),
    PLATFORM_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ECR40003", "플랫폼은 필수 값입니다"),
    RAW_DATA_IS_REQUIRED(HttpStatus.BAD_REQUEST, "ECR40004", "원본 데이터는 필수 값입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

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
