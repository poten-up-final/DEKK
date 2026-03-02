package com.dekk.crawl.presentation.dto.response;

import com.dekk.card.domain.model.enums.Platform;
import com.dekk.crawl.application.result.CrawlBatchResult;

public record CrawlBatchCreateResponse(
        Long batchId,
        Platform platform,
        String status
) {
    public static CrawlBatchCreateResponse from(CrawlBatchResult result) {
        return new CrawlBatchCreateResponse(
                result.batchId(),
                result.platform(),
                result.status()
        );
    }
}
