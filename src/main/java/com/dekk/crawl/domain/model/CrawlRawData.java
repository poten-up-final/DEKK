package com.dekk.crawl.domain.model;

import com.dekk.card.domain.model.enums.Platform;
import com.dekk.common.entity.BaseTimeEntity;
import com.dekk.crawl.domain.exception.CrawlBusinessException;
import com.dekk.crawl.domain.exception.CrawlErrorCode;
import com.dekk.crawl.domain.model.enums.RawDataStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crawl_raw_datas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlRawData extends BaseTimeEntity {

    private static final int MAX_RETRY = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private CrawlBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RawDataStatus status;

    @Column(nullable = false)
    private int retryCount;

    private CrawlRawData(CrawlBatch batch, Platform platform, String rawData) {
        this.batch = batch;
        this.platform = platform;
        this.rawData = rawData;
        this.status = RawDataStatus.PENDING;
        this.retryCount = 0;
    }

    public static CrawlRawData create(CrawlBatch batch, Platform platform, String rawData) {
        if (rawData == null || rawData.isBlank()) {
            throw new CrawlBusinessException(CrawlErrorCode.RAW_DATA_IS_REQUIRED);
        }
        return new CrawlRawData(batch, platform, rawData);
    }

    public void markAsProcessing() {
        this.status = RawDataStatus.PROCESSING;
    }

    public void markAsCompleted() {
        this.status = RawDataStatus.COMPLETED;
    }

    public void fail() {
        this.retryCount++;
        if (this.retryCount >= MAX_RETRY) {
            this.status = RawDataStatus.FAILED;
        } else {
            this.status = RawDataStatus.PENDING;
        }
    }
}
