package com.dekk.app.admin.application.dto.command;

public record AdminLoginCommand(String email, String password, String ip) {}
