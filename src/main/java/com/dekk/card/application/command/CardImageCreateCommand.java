package com.dekk.card.application.command;

public record CardImageCreateCommand(
        String originUrl,
        String imageUrl,
        Boolean isUploaded
) {
}
