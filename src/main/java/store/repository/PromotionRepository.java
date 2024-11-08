package store.repository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        String[] data = validatePromotionData(line);
        promotions.add(createPromotion(data));
    }

    private String[] validatePromotionData(String line) {
        String[] data = line.split(",");
        if (data.length < 5) {
            throw new IllegalArgumentException("잘못된 프로모션 데이터 형식입니다: " + line);
        }
        return data;
    }

    private Promotion createPromotion(String[] data) {
        String name = data[0];
        int buyQuantity = parseInteger(data[1], "프로모션 구매 수량");
        int freeQuantity = parseInteger(data[2], "프로모션 무료 제공 수량");
        LocalDate startDate = parseDate(data[3], "프로모션 시작일");
        LocalDate endDate = parseDate(data[4], "프로모션 종료일");
        return new Promotion(name, buyQuantity, freeQuantity, startDate, endDate);
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + "의 숫자 형식이 잘못되었습니다: " + value, e);
        }
    }

    private LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + "의 날짜 형식이 잘못되었습니다: " + value, e);
        }
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }
}
