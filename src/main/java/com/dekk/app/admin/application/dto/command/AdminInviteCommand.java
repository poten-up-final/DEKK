package com.dekk.app.admin.application.dto.command;

import com.dekk.app.admin.domain.model.AdminRole;

public record AdminInviteCommand(String email, AdminRole role) {}
