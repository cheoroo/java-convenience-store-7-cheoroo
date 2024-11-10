package store.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Promotion {
    private final String name;
    private final PromotionType type;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String name, PromotionType type, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public PromotionType getType() {
        return type;
    }

    public boolean isValidToday() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public int calculateEligibleDiscountUnits(int totalQuantity) {
        int requiredQuantity = getRequiredQuantity();
        int fullPromotions = totalQuantity / requiredQuantity;
        return fullPromotions * requiredQuantity;
    }

    public int calculateDiscount(int quantity, int pricePerUnit) {
        if (!isValidToday()) {
            return 0;
        }
        return type.calculateDiscount(quantity, pricePerUnit);
    }

    public int getRequiredQuantity() {
        return type.getRequiredQuantity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promotion)) return false;
        Promotion promotion = (Promotion) o;
        return Objects.equals(name, promotion.name) &&
                type == promotion.type &&
                Objects.equals(startDate, promotion.startDate) &&
                Objects.equals(endDate, promotion.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, startDate, endDate);
    }
}
