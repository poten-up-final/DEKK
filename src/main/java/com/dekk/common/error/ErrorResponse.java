package com.dekk.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
public record ErrorResponse(
    HttpStatus status,
    String code,
    String message,
    List<String> errors
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.of(errorCode, null);
    }

    public static ErrorResponse of(ErrorCode errorCode, List<String> errors) {
        return new ErrorResponse(
            errorCode.status(),
            errorCode.code(),
            errorCode.message(),
            errors
        );
    }
}
