package com.dekk.app.resource.infrastructure.storage;

import com.dekk.app.resource.domain.dto.PresignedUploadUrl;
import com.dekk.app.resource.domain.exception.ResourceBusinessException;
import com.dekk.app.resource.domain.exception.ResourceErrorCode;
import com.dekk.app.resource.domain.model.enums.ResourceType;
import com.dekk.app.resource.domain.storage.StorageClient;
import com.dekk.app.resource.domain.storage.StorageKeyGenerator;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageClient implements StorageClient {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;

    @Override
    public PresignedUploadUrl generatePresignedUploadUrl(
            ResourceType resourceType, String originalFileName, String contentType) {
        String key = StorageKeyGenerator.generateKey(resourceType, originalFileName);
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

        try {
            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
            return new PresignedUploadUrl(presigned.url().toString(), key, expiration);
        } catch (S3Exception e) {
            log.error("S3 error while generating presigned URL: {}", e.awsErrorDetails(), e);
            throw translateS3Exception(e);
        } catch (SdkClientException e) {
            log.error("SDK client error while generating presigned URL", e);
            throw new ResourceBusinessException(ResourceErrorCode.STORAGE_SERVICE_ERROR);
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (NoSuchKeyException e) {
            log.warn("Attempted to delete non-existent key: {}", key);
            throw new ResourceBusinessException(ResourceErrorCode.RESOURCE_NOT_FOUND);
        } catch (S3Exception e) {
            log.error("S3 error while deleting object: key={}, error={}", key, e.awsErrorDetails(), e);
            throw translateS3Exception(e);
        } catch (SdkClientException e) {
            log.error("SDK client error while deleting object: key={}", key, e);
            throw new ResourceBusinessException(ResourceErrorCode.STORAGE_SERVICE_ERROR);
        }
    }

    @Override
    public String getPublicUrl(String key) {
        return properties.getCdnBaseUrl() + "/" + key;
    }

    private ResourceBusinessException translateS3Exception(S3Exception e) {
        String errorCode = e.awsErrorDetails().errorCode();

        return switch (errorCode) {
            case "NoSuchKey", "NoSuchBucket" -> new ResourceBusinessException(ResourceErrorCode.RESOURCE_NOT_FOUND);
            case "AccessDenied", "InvalidAccessKeyId", "SignatureDoesNotMatch" ->
                new ResourceBusinessException(ResourceErrorCode.STORAGE_ACCESS_DENIED);
            default -> {
                log.error("Unhandled S3 error code: {}", errorCode);
                yield new ResourceBusinessException(ResourceErrorCode.STORAGE_SERVICE_ERROR);
            }
        };
    }
}
