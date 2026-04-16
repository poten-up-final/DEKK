package com.dekk.app.admin.application.dto.command;

public record AdminSignupCommand(String token, String password, String department) {}
