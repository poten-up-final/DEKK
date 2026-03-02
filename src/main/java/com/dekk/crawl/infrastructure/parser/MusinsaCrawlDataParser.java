package com.dekk.crawl.infrastructure.parser;

import com.dekk.card.application.command.CardCreateCommand;
import com.dekk.card.application.command.CardImageCreateCommand;
import com.dekk.card.application.command.ProductCreateCommand;
import com.dekk.card.application.command.ProductImageCreateCommand;
import com.dekk.card.domain.model.enums.Platform;
import com.dekk.card.domain.model.enums.ProductGender;
import com.dekk.crawl.domain.exception.CrawlBusinessException;
import com.dekk.crawl.domain.exception.CrawlErrorCode;
import com.dekk.crawl.domain.parser.CrawlDataParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
        Integer height = parseNullableInt(snap.path("model").path("height"));
        Integer weight = parseNullableInt(snap.path("model").path("weight"));
        ProductGender gender = parseGender(snap.path("model").path("gender"));

        Map<String, String> optionsByGoodsNo = parseOptions(snap);
        Map<String, Boolean> matchedByGoodsNo = parseMatchedFlags(snap);
        List<ProductCreateCommand> products = parseProducts(snap, gender, optionsByGoodsNo, matchedByGoodsNo);

        return new CardCreateCommand(
                cardImage,
                products,
                tags,
                originId,
                true,
                Platform.MUSINSA,
                height,
                weight
        );
    }

    private CardImageCreateCommand parseCardImage(JsonNode snap) {
        JsonNode medias = snap.path("medias");
        String originUrl = medias.has(0) ? medias.get(0).path("path").asText(null) : null;
        return new CardImageCreateCommand(originUrl, null, false);
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

    private Map<String, String> parseOptions(JsonNode snap) {
        Map<String, String> optionsByGoodsNo = new HashMap<>();
        JsonNode goods = snap.path("goods");

        if (!goods.isArray()) {
            return optionsByGoodsNo;
        }

        for (JsonNode good : goods) {
            String goodsNo = good.path("goodsNo").asText(null);

            if (goodsNo == null) {
                continue;
            }

            JsonNode options = good.path("options");

            if (options.isArray() && !options.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");

                for (JsonNode option : options) {
                    String optionName = option.path("optionName").asText(null);

                    if (optionName != null) {
                        joiner.add(optionName);
                    }
                }

                String result = joiner.toString();

                if (!result.isEmpty()) {
                    optionsByGoodsNo.put(goodsNo, result);
                }
            }
        }

        return optionsByGoodsNo;
    }

    private Map<String, Boolean> parseMatchedFlags(JsonNode snap) {
        Map<String, Boolean> matchedByGoodsNo = new HashMap<>();
        JsonNode goods = snap.path("goods");

        if (!goods.isArray()) {
            return matchedByGoodsNo;
        }

        for (JsonNode good : goods) {
            String goodsNo = good.path("goodsNo").asText(null);

            if (goodsNo != null) {
                matchedByGoodsNo.put(goodsNo, good.path("isMatched").asBoolean(false));
            }
        }

        return matchedByGoodsNo;
    }

    private List<ProductCreateCommand> parseProducts(
            JsonNode snap,
            ProductGender gender,
            Map<String, String> optionsByGoodsNo,
            Map<String, Boolean> matchedByGoodsNo
    ) {
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

            String imageUrl = detail.path("imageUrl").asText(null);
            ProductImageCreateCommand productImage = new ProductImageCreateCommand(imageUrl, null, false);

            String option = optionsByGoodsNo.get(goodsNo);
            boolean isMatched = matchedByGoodsNo.getOrDefault(goodsNo, false);

            ProductCreateCommand product = new ProductCreateCommand(
                    productImage,
                    detail.path("brandName").asText(null),
                    detail.path("goodsName").asText(null),
                    parseNullableInt(detail.path("price")),
                    goodsNo,
                    option,
                    !isMatched,
                    detail.path("linkUrl").asText(null),
                    gender
            );

            products.add(product);
        }

        return products;
    }

    private ProductGender parseGender(JsonNode genderNode) {
        if (genderNode.isMissingNode() || genderNode.isNull()) {
            return null;
        }

        String value = genderNode.asText().toUpperCase();
        return ProductGender.musinsaParse(value);
    }

    private Integer parseNullableInt(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }

        return node.asInt();
    }
}
