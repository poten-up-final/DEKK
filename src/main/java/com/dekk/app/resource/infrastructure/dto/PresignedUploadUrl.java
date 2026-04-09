package com.dekk.app.resource.infrastructure.dto;

import java.time.Duration;

public record PresignedUploadUrl(String url, Duration expiration) {}
