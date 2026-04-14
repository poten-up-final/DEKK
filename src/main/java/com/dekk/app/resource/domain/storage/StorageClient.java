package com.dekk.app.resource.domain.storage;

import com.dekk.app.resource.domain.dto.PresignedUploadUrl;
import com.dekk.app.resource.domain.model.enums.ResourceType;

public interface StorageClient {
    PresignedUploadUrl generatePresignedUploadUrl(
            ResourceType resourceType, String originalFileName, String contentType);

    void delete(String key);

    String getPublicUrl(String key);
}
