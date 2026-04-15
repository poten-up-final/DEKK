package com.dekk.app.card.application.dto.command;

public record ProductCreateByUserCommand(Long resourceId, String brand, String name, String productUrl) {}
