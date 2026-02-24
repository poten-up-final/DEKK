package com.dekk.card.application.command;

import com.dekk.card.domain.model.enums.Platform;

import java.util.List;

public record CardCreateCommand(
    CardImageCreateCommand cardImage,
    List<ProductCreateCommand> productCreateCommands,
    String tags,
    String originId,
    boolean isActive,
    Platform platform,
    Double height,
    Double weight
) {
}
