package com.dekk.app.resource.domain.storage;

import com.dekk.app.resource.domain.dto.PresignedUploadUrl;

public interface StorageClient {
    PresignedUploadUrl generatePresignedUploadUrl(String key, String contentType);

    void delete(String key);

    String getPublicUrl(String key);
}
