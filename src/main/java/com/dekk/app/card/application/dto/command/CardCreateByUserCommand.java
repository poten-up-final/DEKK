package com.dekk.app.card.application.dto.command;

import com.dekk.app.card.domain.model.enums.TargetGender;
import java.util.List;

public record CardCreateByUserCommand(
        Long resourceId,
        TargetGender targetGender,
        Integer height,
        Integer weight,
        String tags,
        List<ProductCreateByUserCommand> products) {}
