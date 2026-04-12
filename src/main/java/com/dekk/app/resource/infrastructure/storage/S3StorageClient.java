package com.dekk.app.resource.infrastructure.storage;

import com.dekk.app.resource.domain.dto.PresignedUploadUrl;
import com.dekk.app.resource.domain.storage.StorageClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3StorageClient implements StorageClient {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;

    @Override
    public PresignedUploadUrl generatePresignedUploadUrl(String key, String contentType) {
        Duration expiration = properties.getPresignedUrlExpiration();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putRequest)
                .signatureDuration(expiration)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return new PresignedUploadUrl(presigned.url().toString(), expiration);
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    @Override
    public String getPublicUrl(String key) {
        return properties.getCdnBaseUrl() + "/" + key;
    }
}
