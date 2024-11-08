package store.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.Promotion;
import store.utils.FileLoader;

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

        promotions.add(new Promotion(name, buyQuantity, freeQuantity, startDate, endDate));
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }
}
