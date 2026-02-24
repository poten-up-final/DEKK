package com.dekk.activelog.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "미평가 카드 응답 DTO")
public record UnseenCardResponse(
    @Schema(description = "카드 고유 ID", example = "105")
    Long cardId,

    @Schema(description = "카드(상품) 이미지 URL", example = "https://image.msscdn.net/images/goods_img/...")
    String imageUrl,

    @Schema(description = "입점 플랫폼(브랜드) 명", example = "무신사")
    String brandName,

    @Schema(description = "상품명", example = "오버핏 스트라이프 셔츠")
    String productName,

    @Schema(description = "상품 가격", example = "45000")
    Integer price
) {
    public static UnseenCardResponse from(Long cardId, String imageUrl, String brandName, String productName, Integer price) {
        return new UnseenCardResponse(cardId, imageUrl, brandName, productName, price);
    }
}
