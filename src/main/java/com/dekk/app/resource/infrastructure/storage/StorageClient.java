package com.dekk.app.resource.infrastructure.storage;

import com.dekk.app.resource.infrastructure.dto.PresignedUploadUrl;

public interface StorageClient {
    PresignedUploadUrl generatePresignedUploadUrl(String key, String contentType);

    void delete(String key);

    String getPublicUrl(String key);
}
