package com.dekk.crawl.presentation.dto.response;

import com.dekk.crawl.application.result.CrawlRawDataResult;

public record CrawlRawDataCreateResponse(
        Long rawDataId,
        String status
) {
    public static CrawlRawDataCreateResponse from(CrawlRawDataResult result) {
        return new CrawlRawDataCreateResponse(
                result.rawDataId(),
                result.status()
        );
    }
}
