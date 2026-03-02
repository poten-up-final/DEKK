package com.dekk.common.scheduler;

import com.dekk.crawl.application.service.CrawlProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlScheduler {

    private final CrawlProcessingService processingService;

    @Scheduled(fixedDelay = 600_000)
    public void processNextBatch() {
        processingService.processNextBatch();
    }
}
