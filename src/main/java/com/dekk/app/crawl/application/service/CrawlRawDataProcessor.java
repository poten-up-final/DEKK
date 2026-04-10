package com.dekk.app.crawl.application.service;

import com.dekk.app.card.application.dto.command.CardCreateCommand;
import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.repository.CardRepository;
import com.dekk.app.crawl.domain.exception.CrawlBusinessException;
import com.dekk.app.crawl.domain.exception.CrawlErrorCode;
import com.dekk.app.crawl.domain.model.CrawlRawData;
import com.dekk.app.crawl.domain.repository.CrawlRawDataRepository;
import com.dekk.app.crawl.infrastructure.parser.CrawlDataParserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrawlRawDataProcessor {

    private static final Logger log = LoggerFactory.getLogger("CRAWL");

    private final CrawlDataParserFactory parsers;
    private final CardRepository cardRepository;
    private final CrawlRawDataRepository rawDataRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(Long rawDataId) {
        CrawlRawData rawData = rawDataRepository
                .findById(rawDataId)
                .orElseThrow(() -> new CrawlBusinessException(CrawlErrorCode.RAW_DATA_NOT_FOUND));

        rawData.markAsProcessing();

        try {
            List<CardCreateCommand> commands = parseRawData(rawData);
            saveNewCards(commands);
            rawData.markAsCompleted();
        } catch (JsonProcessingException e) {
            log.error("raw data 처리 실패: rawDataId={}", rawDataId, e);
            rawData.fail();
        }
    }

    private List<CardCreateCommand> parseRawData(CrawlRawData rawData) throws JsonProcessingException {
        return parsers.getParser(rawData.getPlatform()).parse(rawData.getRawData());
    }

    private void saveNewCards(List<CardCreateCommand> commands) {
        List<Card> cards = commands.stream()
                .collect(Collectors.toMap(
                        cmd -> cmd.platform() + "_" + cmd.originId(), cmd -> cmd, (existing, replacement) -> existing))
                .values()
                .stream()
                .filter(cmd -> !cardRepository.existsByPlatformAndOriginId(cmd.platform(), cmd.originId()))
                .map(Card::createByCrawl)
                .toList();
        cardRepository.saveAll(cards);
    }
}
