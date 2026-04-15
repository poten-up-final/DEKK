package com.dekk.app.card.presentation.response;

import com.dekk.app.card.application.dto.result.AdminCardDetailResult;
import com.dekk.app.card.application.dto.result.AdminCardDetailResult.CardImageDetail;
import com.dekk.app.card.application.dto.result.AdminCardDetailResult.CategoryDetail;
import com.dekk.app.card.application.dto.result.AdminCardDetailResult.ProductDetail;
import com.dekk.app.card.application.dto.result.AdminCardDetailResult.ProductImageDetail;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.Platform;
import com.dekk.app.card.domain.model.enums.TargetGender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "관리자 카드 상세 응답")
public record AdminCardDetailResponse(
        @Schema(description = "카드 ID", example = "1") Long cardId,
        @Schema(description = "원본 ID", example = "abc123") String originId,
        @Schema(description = "카드 상태", example = "PENDING") CardStatus status,
        @Schema(description = "플랫폼", example = "MUSINSA") Platform platform,
        @Schema(description = "타겟 성별", example = "MEN") TargetGender targetGender,
        @Schema(description = "키(cm)", example = "175") Integer height,
        @Schema(description = "몸무게(kg)", example = "65") Integer weight,

        @Schema(description = "태그 목록", example = "[\"캐주얼\", \"스트릿\"]")
        List<String> tags,

        @Schema(description = "카드 이미지") CardImageResponse cardImage,
        @Schema(description = "상품 목록") List<ProductResponse> products,
        @Schema(description = "카테고리 목록") List<CategoryResponse> categories,
        @Schema(description = "생성일시") LocalDateTime createdAt,
        @Schema(description = "수정일시") LocalDateTime updatedAt) {

    @Schema(description = "카드 이미지 응답")
    public record CardImageResponse(
            @Schema(description = "이미지 ID", example = "1") Long imageId,
            @Schema(description = "원본 이미지 URL") String originUrl,
            @Schema(description = "이미지 URL") String imageUrl) {

        public static CardImageResponse from(CardImageDetail detail) {
            return new CardImageResponse(detail.imageId(), detail.originUrl(), detail.imageUrl());
        }
    }

    @Schema(description = "상품 응답")
    public record ProductResponse(
            @Schema(description = "상품 ID", example = "10") Long productId,
            @Schema(description = "브랜드", example = "Nike") String brand,

            @Schema(description = "상품명", example = "에어맥스 90")
            String name,

            @Schema(description = "상품 URL") String productUrl,
            @Schema(description = "활성 여부") boolean isActive,
            @Schema(description = "상품 이미지") ProductImageResponse productImage) {

        public static ProductResponse from(ProductDetail detail) {
            ProductImageResponse imageResponse =
                    detail.productImage() != null ? ProductImageResponse.from(detail.productImage()) : null;
            return new ProductResponse(
                    detail.productId(),
                    detail.brand(),
                    detail.name(),
                    detail.productUrl(),
                    detail.isActive(),
                    imageResponse);
        }
    }

    @Schema(description = "상품 이미지 응답")
    public record ProductImageResponse(
            @Schema(description = "이미지 ID", example = "1") Long imageId,
            @Schema(description = "원본 이미지 URL") String originUrl,
            @Schema(description = "이미지 URL") String imageUrl) {

        public static ProductImageResponse from(ProductImageDetail detail) {
            return new ProductImageResponse(detail.imageId(), detail.originUrl(), detail.imageUrl());
        }
    }

    @Schema(description = "카테고리 응답")
    public record CategoryResponse(
            @Schema(description = "카테고리 ID", example = "1") Long categoryId,
            @Schema(description = "카테고리명", example = "상의") String name,
            @Schema(description = "하위 카테고리 목록") List<ChildCategoryResponse> children) {

        public static CategoryResponse from(CategoryDetail detail) {
            return new CategoryResponse(
                    detail.categoryId(),
                    detail.name(),
                    detail.children().stream()
                            .map(child -> new ChildCategoryResponse(child.categoryId(), child.name()))
                            .toList());
        }
    }

    @Schema(description = "하위 카테고리 응답")
    public record ChildCategoryResponse(
            @Schema(description = "카테고리 ID", example = "3") Long categoryId,
            @Schema(description = "카테고리명", example = "티셔츠") String name) {}

    public static AdminCardDetailResponse from(AdminCardDetailResult result) {
        return new AdminCardDetailResponse(
                result.cardId(),
                result.originId(),
                result.status(),
                result.platform(),
                result.targetGender(),
                result.height(),
                result.weight(),
                result.tags(),
                CardImageResponse.from(result.cardImage()),
                result.products().stream().map(ProductResponse::from).toList(),
                result.categories().stream().map(CategoryResponse::from).toList(),
                result.createdAt(),
                result.updatedAt());
    }
}
