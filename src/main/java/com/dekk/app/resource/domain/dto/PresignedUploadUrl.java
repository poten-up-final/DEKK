package com.dekk.app.resource.domain.dto;

import java.time.Duration;

public record PresignedUploadUrl(String url, String key, Duration expiration) {}
