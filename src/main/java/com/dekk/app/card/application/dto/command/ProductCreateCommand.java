package com.dekk.app.card.application.dto.command;

public record ProductCreateCommand(
        ProductImageCreateCommand productImage,
        String brand,
        String name,
        String originId,
        String productUrl,
        boolean isActive) {}
