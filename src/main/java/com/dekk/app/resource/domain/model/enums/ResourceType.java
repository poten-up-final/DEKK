package com.dekk.app.resource.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceType {
    CARD_IMAGE("카드 이미지"),
    PRODUCT_IMAGE("상품 이미지");

    private final String description;

    public String getKeyPrefix() {
        return switch (this) {
            case CARD_IMAGE -> "card-images";
            case PRODUCT_IMAGE -> "product-images";
        };
    }

    public String getOriginalKeyPrefix() {
        return "original/" + getKeyPrefix();
    }
}
