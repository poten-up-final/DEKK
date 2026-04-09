package com.dekk.app.resource.domain.model;

import com.dekk.app.resource.domain.exception.ResourceBusinessException;
import com.dekk.app.resource.domain.exception.ResourceErrorCode;
import com.dekk.app.resource.domain.model.enums.ResourceStatus;
import com.dekk.app.resource.domain.model.enums.ResourceType;
import com.dekk.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "resources")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resource extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 20)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ResourceStatus status;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "original_key", length = 500)
    private String originalKey;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "processed_key", length = 500)
    private String processedKey;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    private Resource(ResourceType resourceType, String originalKey, String originalFileName, LocalDateTime expiresAt) {
        this.publicId = UUID.randomUUID();
        this.resourceType = resourceType;
        this.status = ResourceStatus.PENDING;
        this.originalKey = originalKey;
        this.originalFileName = originalFileName;
        this.expiresAt = expiresAt;
    }

    public static Resource createPending(
            ResourceType resourceType, String originalKey, String originalFileName, LocalDateTime expiresAt) {
        validateCreatePendingParameters(resourceType, originalKey, originalFileName, expiresAt);
        return new Resource(resourceType, originalKey, originalFileName, expiresAt);
    }

    private static void validateCreatePendingParameters(
            ResourceType resourceType, String originalKey, String originalFileName, LocalDateTime expiresAt) {
        if (resourceType == null) {
            throw new ResourceBusinessException(ResourceErrorCode.RESOURCE_TYPE_IS_REQUIRED);
        }

        if (originalKey == null || originalKey.isBlank()) {
            throw new ResourceBusinessException(ResourceErrorCode.ORIGINAL_KEY_IS_REQUIRED);
        }

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new ResourceBusinessException(ResourceErrorCode.ORIGINAL_FILE_NAME_IS_REQUIRED);
        }

        if (originalFileName.length() > 255) {
            throw new ResourceBusinessException(ResourceErrorCode.ORIGINAL_FILE_NAME_TOO_LONG);
        }

        if (expiresAt == null) {
            throw new ResourceBusinessException(ResourceErrorCode.EXPIRES_AT_IS_REQUIRED);
        }
    }

    public void markAsProcessed(String processedKey, String imageUrl, Long fileSize, String contentType) {
        this.status = ResourceStatus.PROCESSED;
        this.processedKey = processedKey;
        this.imageUrl = imageUrl;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public void markAsFailed() {
        this.status = ResourceStatus.FAILED;
    }

    public void markAsExpired() {
        this.status = ResourceStatus.EXPIRED;
    }

    public boolean isProcessed() {
        return this.status.isProcessed();
    }

    public boolean canBeUsed() {
        return this.status.canBeUsed();
    }

    public boolean isCardImage() {
        return this.resourceType == ResourceType.CARD_IMAGE;
    }

    public boolean isProductImage() {
        return this.resourceType == ResourceType.PRODUCT_IMAGE;
    }
}
