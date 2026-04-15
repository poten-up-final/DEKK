package com.dekk.app.card.application.dto.result;

import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.model.CardImage;
import com.dekk.app.card.domain.model.CardProduct;
import com.dekk.app.card.domain.model.Product;
import com.dekk.app.card.domain.model.ProductImage;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.Platform;
import com.dekk.app.card.domain.model.enums.TargetGender;
import com.dekk.app.category.application.dto.CategoryListResult;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record AdminCardDetailResult(
        Long cardId,
        String originId,
        CardStatus status,
        Platform platform,
        TargetGender targetGender,
        Integer height,
        Integer weight,
        List<String> tags,
        CardImageDetail cardImage,
        List<ProductDetail> products,
        List<CategoryDetail> categories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public record CardImageDetail(Long imageId, String originUrl, String imageUrl) {
        public static CardImageDetail from(CardImage cardImage) {
            return new CardImageDetail(cardImage.getId(), cardImage.getOriginUrl(), cardImage.getImageUrl());
        }
    }

    public record ProductDetail(
            Long productId,
            String brand,
            String name,
            String productUrl,
            boolean isActive,
            ProductImageDetail productImage) {

        public static ProductDetail from(Product product) {
            ProductImageDetail imageDetail =
                    product.getProductImage() != null ? ProductImageDetail.from(product.getProductImage()) : null;
            return new ProductDetail(
                    product.getId(),
                    product.getBrand(),
                    product.getName(),
                    product.getProductUrl(),
                    product.isActive(),
                    imageDetail);
        }
    }

    public record ProductImageDetail(Long imageId, String originUrl, String imageUrl) {
        public static ProductImageDetail from(ProductImage productImage) {
            return new ProductImageDetail(
                    productImage.getId(), productImage.getOriginUrl(), productImage.getImageUrl());
        }
    }

    public record CategoryDetail(Long categoryId, String name, List<ChildCategoryDetail> children) {
        public static CategoryDetail from(CategoryListResult result) {
            return new CategoryDetail(
                    result.categoryId(),
                    result.name(),
                    result.children().stream()
                            .map(child -> new ChildCategoryDetail(child.categoryId(), child.name()))
                            .toList());
        }
    }

    public record ChildCategoryDetail(Long categoryId, String name) {}

    public static AdminCardDetailResult of(Card card, List<CategoryListResult> categoryResults) {
        List<ProductDetail> productDetails = card.getCardProducts().stream()
                .map(CardProduct::getProduct)
                .map(ProductDetail::from)
                .toList();

        List<CategoryDetail> categoryDetails =
                categoryResults.stream().map(CategoryDetail::from).toList();

        return new AdminCardDetailResult(
                card.getId(),
                card.getOriginId(),
                card.getStatus(),
                card.getPlatform(),
                card.getTargetGender(),
                card.getHeight(),
                card.getWeight(),
                parseTags(card.getTags()),
                CardImageDetail.from(card.getCardImage()),
                productDetails,
                categoryDetails,
                card.getCreatedAt(),
                card.getUpdatedAt());
    }

    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
