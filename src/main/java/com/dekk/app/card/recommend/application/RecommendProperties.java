package com.dekk.app.card.recommend.application;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "recommend")
public record RecommendProperties(
        @DecimalMin("0.0") @DecimalMax("1.0") double ratio,
        @Min(1) int deepScrollPageThreshold,
        @Min(1) int largeCandidateThreshold) {}
