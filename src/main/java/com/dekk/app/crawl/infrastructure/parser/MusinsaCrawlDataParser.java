package com.dekk.app.crawl.infrastructure.parser;

import com.dekk.app.card.application.dto.command.CardCreateCommand;
import com.dekk.app.card.application.dto.command.CardImageCreateCommand;
import com.dekk.app.card.application.dto.command.ProductCreateCommand;
import com.dekk.app.card.application.dto.command.ProductImageCreateCommand;
import com.dekk.app.card.domain.model.enums.Platform;
import com.dekk.app.card.domain.model.enums.TargetGender;
import com.dekk.app.crawl.domain.parser.CrawlDataParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusinsaCrawlDataParser implements CrawlDataParser {

    private final ObjectMapper objectMapper;

    @Override
    public Platform supportedPlatform() {
        return Platform.MUSINSA;
    }

    @Override
    public List<CardCreateCommand> parse(String rawData) throws JsonProcessingException {
        JsonNode rootArray = objectMapper.readTree(rawData);

        List<CardCreateCommand> commands = new ArrayList<>();

        for (JsonNode snap : rootArray) {
            CardCreateCommand command = parseSnap(snap);

            if (command != null) {
                commands.add(command);
            }
        }

        return commands;
    }

    private CardCreateCommand parseSnap(JsonNode snap) {
        String originId = snap.path("id").asText(null);

        if (originId == null) {
            return null;
        }

        CardImageCreateCommand cardImage = parseCardImage(snap);
        String tags = parseTags(snap);
        TargetGender targetGender = parseGender(snap.path("model").path("gender"));
        Integer height = parseNullableInt(snap.path("model").path("height"));
        Integer weight = parseNullableInt(snap.path("model").path("weight"));

        List<ProductCreateCommand> products = parseProducts(snap);

        return new CardCreateCommand(
                cardImage, products, tags, originId, Platform.MUSINSA, targetGender, height, weight);
    }

    private CardImageCreateCommand parseCardImage(JsonNode snap) {
        JsonNode medias = snap.path("medias");

        if (!medias.isArray() || medias.isEmpty()) {
            return new CardImageCreateCommand(null, null, false);
        }

        JsonNode first = medias.get(0);
        String originUrl = first.path("originUrl").asText(null);
        String imageUrl = first.path("imageUrl").asText(null);
        boolean isUploaded = first.path("isUploaded").asBoolean(false);

        return new CardImageCreateCommand(originUrl, imageUrl, isUploaded);
    }

    private String parseTags(JsonNode snap) {
        JsonNode tagsNode = snap.path("tags");

        if (!tagsNode.isArray() || tagsNode.isEmpty()) {
            return null;
        }

        StringJoiner joiner = new StringJoiner(",");

        for (JsonNode tag : tagsNode) {
            String name = tag.path("name").asText(null);

            if (name != null) {
                joiner.add(name);
            }
        }

        String result = joiner.toString();
        return result.isEmpty() ? null : result;
    }

    private List<ProductCreateCommand> parseProducts(JsonNode snap) {
        List<ProductCreateCommand> products = new ArrayList<>();
        JsonNode detailList = snap.path("goods_detail_list");

        if (!detailList.isArray()) {
            return products;
        }

        for (JsonNode detail : detailList) {
            String goodsNo = detail.path("goodsNo").asText(null);

            if (goodsNo == null) {
                continue;
            }

            String originUrl = detail.path("originUrl").asText(null);
            String imageUrl = detail.path("imageUrl").asText(null);
            boolean isUploaded = detail.path("isUploaded").asBoolean(false);
            ProductImageCreateCommand productImage = new ProductImageCreateCommand(originUrl, imageUrl, isUploaded);

            boolean isActive = imageUrl != null && !imageUrl.isEmpty();

            ProductCreateCommand product = new ProductCreateCommand(
                    productImage,
                    detail.path("brandName").asText(null),
                    detail.path("goodsName").asText(null),
                    goodsNo,
                    detail.path("linkUrl").asText(null),
                    isActive);

            products.add(product);
        }

        return products;
    }

    private TargetGender parseGender(JsonNode genderNode) {
        if (genderNode.isMissingNode() || genderNode.isNull()) {
            return null;
        }

        String value = genderNode.asText().toUpperCase();
        return TargetGender.musinsaParse(value);
    }

    private Integer parseNullableInt(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }

        return node.asInt();
    }
}
