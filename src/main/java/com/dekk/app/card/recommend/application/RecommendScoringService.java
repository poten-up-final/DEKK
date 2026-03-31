package com.dekk.app.card.recommend.application;

import com.dekk.app.card.application.dto.result.MemberCardResult;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RecommendScoringService {

    private static final double HEIGHT_RANGE = 5.0;
    private static final double WEIGHT_RANGE = 7.0;
    private static final double MAX_SCORE = 1.0;
    private static final double MIN_SCORE = 0.0;
    private static final double SCORE_DIMENSION_COUNT = 2.0;
    private static final double CATEGORY_WEIGHT = 0.6;
    private static final double BODY_WEIGHT = 0.4;

    private double calculateBodyScore(int userHeight, int userWeight, Integer cardHeight, Integer cardWeight) {
        double heightScore = calculateDimensionScore(userHeight, cardHeight, HEIGHT_RANGE);
        double weightScore = calculateDimensionScore(userWeight, cardWeight, WEIGHT_RANGE);
        return (heightScore + weightScore) / SCORE_DIMENSION_COUNT;
    }

    public Map<Long, Double> calculateCategoryPreferenceRatios(List<Long> likedCategoryIds) {
        if (likedCategoryIds.isEmpty()) {
            return Map.of();
        }

        long total = likedCategoryIds.size();
        return likedCategoryIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (double) e.getValue() / total));
    }

    public List<MemberCardResult> rank(
            Integer userHeight,
            Integer userWeight,
            List<MemberCardResult> candidates,
            Map<Long, List<Long>> cardCategoryMap,
            Map<Long, Double> preferences) {

        return candidates.stream()
                .map(card -> Map.entry(card, totalScore(userHeight, userWeight, card, cardCategoryMap, preferences)))
                .sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private double calculateCategoryScore(List<Long> cardCategoryIds, Map<Long, Double> preferences) {
        if (cardCategoryIds.isEmpty() || preferences.isEmpty()) {
            return MIN_SCORE;
        }

        return cardCategoryIds.stream()
                .filter(preferences::containsKey)
                .mapToDouble(preferences::get)
                .average()
                .orElse(MIN_SCORE);
    }

    private double calculateDimensionScore(int userValue, Integer cardValue, double range) {
        if (cardValue == null) {
            return MAX_SCORE;
        }
        double diff = Math.abs(userValue - cardValue);
        double rawScore = MAX_SCORE - diff / range;

        return Math.max(MIN_SCORE, rawScore);
    }

    private double totalScore(
            Integer userHeight,
            Integer userWeight,
            MemberCardResult card,
            Map<Long, List<Long>> cardCategoryMap,
            Map<Long, Double> preferences) {

        List<Long> cardCategoryIds = cardCategoryMap.getOrDefault(card.cardId(), List.of());
        double categoryScore = calculateCategoryScore(cardCategoryIds, preferences);

        double bodyScore = (userHeight == null || userWeight == null)
                ? MAX_SCORE
                : calculateBodyScore(userHeight, userWeight, card.height(), card.weight());

        return categoryScore * CATEGORY_WEIGHT + bodyScore * BODY_WEIGHT;
    }
}
