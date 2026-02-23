package com.dekk.card.application.command;

public record ProductImageCreateCommand(
        String originUrl,
        String imageUrl,
        Boolean isUploaded
) {
}
