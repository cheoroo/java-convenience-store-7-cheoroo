package store.repository;

import store.domain.Promotion;
import store.domain.PromotionType;
import store.utils.FileLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepository {
    private final List<Promotion> promotions = new ArrayList<>();

    public PromotionRepository() {
        loadPromotions();
    }

    private void loadPromotions() {
        FileLoader.loadFile("/promotions.md", this::processLine);
    }

    private void processLine(String line) {
        String[] data = line.split(",");
        String name = data[0];
        int buyQuantity = Integer.parseInt(data[1]);
        int freeQuantity = Integer.parseInt(data[2]);
        LocalDate startDate = LocalDate.parse(data[3]);
        LocalDate endDate = LocalDate.parse(data[4]);

        PromotionType type = getPromotionType(buyQuantity, freeQuantity);
        promotions.add(new Promotion(name, type, startDate, endDate));
    }

    private PromotionType getPromotionType(int buyQuantity, int freeQuantity) {
        if (buyQuantity == 1 && freeQuantity == 1) {
            return PromotionType.BUY_ONE_GET_ONE_FREE;
        } else if (buyQuantity == 2 && freeQuantity == 1) {
            return PromotionType.BUY_TWO_GET_ONE_FREE;
        }
        throw new IllegalArgumentException("지원하지 않는 프로모션 타입입니다: " + buyQuantity + "+" + freeQuantity);
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }
}
