package com.dekk.card.application.command;

import com.dekk.card.domain.model.enums.Platform;

import java.util.List;

public record CardCreateCommand(
    CardImageCreateCommand cardImage,
    CardRawDataCommand cardRawData,
    List<ProductCreateCommand> productCreateCommands,
    List<Long> tagIds,
    String originId,
    Boolean isActive,
    Platform platform,
    Integer height,
    Integer weight
) {
}
