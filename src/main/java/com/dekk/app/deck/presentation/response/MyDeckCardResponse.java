package com.dekk.app.deck.presentation.response;

import com.dekk.app.deck.application.dto.result.MyDeckCardResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "내 보관함 카드 상세 응답")
public record MyDeckCardResponse(
        @Schema(description = "카드 ID", example = "1") Long cardId,
        @Schema(description = "카드 이미지 URL") String cardImageUrl,
        @Schema(description = "키(cm)", example = "175") Integer height,
        @Schema(description = "몸무게(kg)", example = "65") Integer weight,
        @Schema(description = "태그 목록") List<String> tag,
        @Schema(description = "상품 목록") List<ProductDetailResponse> products) {
    @Schema(description = "보관함 카드 내 상품 상세 응답")
    public record ProductDetailResponse(
            @Schema(description = "브랜드명", example = "Nike") String brand,
            @Schema(description = "상품 구매 링크") String url,
            @Schema(description = "상품명", example = "에어포스") String name,
            @Schema(description = "상품 이미지 URL") String productsImageUrl) {
        public static ProductDetailResponse from(MyDeckCardResult.ProductDetail result) {
            return new ProductDetailResponse(result.brand(), result.url(), result.name(), result.productsImageUrl());
        }
    }

    public static MyDeckCardResponse from(MyDeckCardResult result) {
        return new MyDeckCardResponse(
                result.cardId(),
                result.cardImageUrl(),
                result.height(),
                result.weight(),
                result.tag(),
                result.products().stream().map(ProductDetailResponse::from).toList());
    }
}
