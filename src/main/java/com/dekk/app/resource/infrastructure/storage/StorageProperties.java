package com.dekk.app.resource.infrastructure.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    @NotBlank(message = "Storage region must be configured")
    private String region;

    @NotBlank(message = "Storage bucket must be configured")
    private String bucket;

    @NotNull(message = "Presigned URL expiration must be configured")
    private Duration presignedUrlExpiration;

    private String cdnBaseUrl;
}
