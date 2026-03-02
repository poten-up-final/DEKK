package com.dekk.crawl.presentation.dto.request;

import com.dekk.crawl.application.command.CrawlRawDataCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record CrawlRawDataCreateRequest(
        @NotBlank(message = "원본 데이터는 필수 값입니다.")
        String rawData
) {
    public CrawlRawDataCreateCommand toCommand(Long batchId) {
        return new CrawlRawDataCreateCommand(batchId, rawData);
    }
}
