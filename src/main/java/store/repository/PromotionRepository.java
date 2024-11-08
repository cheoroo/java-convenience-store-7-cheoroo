package store.repository;

import store.domain.Promotion;
import store.parser.PromotionParser;
import store.utils.FileLoader;

import java.util.ArrayList;
import java.util.List;

public class PromotionRepository {
    private final List<Promotion> promotions = new ArrayList<>();
    private final PromotionParser promotionParser = new PromotionParser();

    public PromotionRepository() {
        loadPromotions();
    }

    private void loadPromotions() {
        FileLoader.loadFile("/promotions.md", this::processLine);
    }

    private void processLine(String line) {
        promotions.add(promotionParser.parsePromotion(line));
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }
}
