package com.dekk.app.resource.domain.storage;

import com.dekk.app.resource.domain.exception.ResourceBusinessException;
import com.dekk.app.resource.domain.exception.ResourceErrorCode;
import com.dekk.app.resource.domain.model.enums.ResourceType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

public class StorageKeyGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    public static String generateKey(ResourceType resourceType, String originalFileName) {
        if (resourceType == null) {
            throw new ResourceBusinessException(ResourceErrorCode.RESOURCE_TYPE_IS_REQUIRED);
        }

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new ResourceBusinessException(ResourceErrorCode.ORIGINAL_FILE_NAME_IS_REQUIRED);
        }

        String yearMonth = LocalDate.now().format(DATE_FORMATTER);
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFileName);

        return String.format("%s/%s/%s%s", resourceType.getKeyPrefix(), yearMonth, uuid, extension);
    }

    private static String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex >= filename.length() - 1) {
            throw new ResourceBusinessException(ResourceErrorCode.FILE_EXTENSION_IS_REQUIRED);
        }

        String extension = filename.substring(dotIndex).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResourceBusinessException(ResourceErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }

        return extension;
    }
}
