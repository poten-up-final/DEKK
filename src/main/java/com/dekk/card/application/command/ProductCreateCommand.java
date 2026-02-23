package com.dekk.card.application.command;

import com.dekk.card.domain.model.enums.ProductGender;

public record ProductCreateCommand(
    ProductImageCreateCommand productImage,
    ProductRawDataCommand productRawData,
    String name,
    Integer price,
    String externalProductId,
    String option,
    Boolean isSimilar,
    String productUrl,
    ProductGender gender
) {
}
