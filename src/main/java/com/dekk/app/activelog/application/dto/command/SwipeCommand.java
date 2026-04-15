package com.dekk.app.activelog.application.dto.command;

import com.dekk.app.activelog.domain.model.SwipeType;
import java.util.UUID;

public record SwipeCommand(Long userId, UUID cardPublicId, SwipeType swipeType) {}
