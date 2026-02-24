package com.dekk.card.application.command;

import com.dekk.card.domain.model.enums.ProductGender;

public record ProductCreateCommand(
    ProductImageCreateCommand productImage,
    String name,
    Integer price,
    String originId,
    String option,
    Boolean isSimilar,
    String productUrl,
    ProductGender gender
) {
}
