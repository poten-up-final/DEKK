package com.dekk.app.resource.infrastructure.storage;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String region;
    private String bucket;
    private Duration presignedUrlExpiration;
    private String cdnBaseUrl;
}
