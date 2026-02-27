package com.dekk.crawl.domain.repository;

import com.dekk.crawl.domain.model.CrawlBatch;
import java.util.Optional;

public interface CrawlBatchRepository {

    CrawlBatch save(CrawlBatch batch);

    Optional<CrawlBatch> findById(Long id);
}
