package com.dekk.user.application.command;

import com.dekk.user.domain.model.enums.Gender;

public record UserProfileUpdateCommand(String nickname, Double height, Double weight, Gender gender) {
}
