package com.dekk.user.application.command;

import com.dekk.user.domain.model.enums.Gender;

public record UserOnboardingCommand(String nickname, Double height, Double weight, Gender gender) {
}
