package com.dekk.app.deck.presentation.response;

import com.dekk.app.deck.application.dto.result.ShareTokenResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쉐어덱 토큰 발급 응답")
public record ShareTokenResponse(
        @Schema(description = "초대 토큰", example = "a1b2c3d4e5f64789b123c456d789e012")
        String token,

        @Schema(description = "토큰 만료 남은 시간(초)", example = "86400")
        long expiredInSeconds) {
    public static ShareTokenResponse from(ShareTokenResult result) {
        return new ShareTokenResponse(result.token(), result.expiredInSeconds());
    }
}
