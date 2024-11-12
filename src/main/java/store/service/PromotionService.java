package store.service;

import store.domain.Product;
import store.domain.Promotion;
import store.repository.PromotionRepository;

import java.util.Optional;

public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Optional<Promotion> getApplicablePromotion(Product product) {
        return promotionRepository.getPromotions().stream()
                .filter(promotion -> promotion.getName().equals(product.getPromotion()))
                .findFirst();
    }

    public int calculateFreeQuantity(int quantity, Product product) {
        Optional<Promotion> promotion = getApplicablePromotion(product);
        if (promotion.isEmpty() || !promotion.get().isValidToday()) {
            return 0;
        }

        Promotion p = promotion.get();
        int requiredQuantity = p.getRequiredQuantity();
        int sets = quantity / requiredQuantity;
        return sets * p.getType().getFreeQuantity();
    }

    public String checkPromotionAddFreeAvailability(Product product, int desiredQuantity) {
        Optional<Promotion> promotion = getApplicablePromotion(product);
        if (promotion.isEmpty() || !promotion.get().isValidToday()) {
            return "";
        }

        int requiredQuantity = promotion.get().getRequiredQuantity();
        int remainder = desiredQuantity % requiredQuantity;
        int additionalQuantity = remainder == 0 ? 0 : requiredQuantity - remainder;

        if (additionalQuantity > 0) {
            return String.format("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)", product.getName(), additionalQuantity);
        }
        return "";
    }

    public String checkPromotionInsufficientDiscountAvailability(Product product, int desiredQuantity) {
        Optional<Promotion> promotion = getApplicablePromotion(product);
        if (promotion.isEmpty() || !promotion.get().isValidToday()) {
            return "";
        }

        int requiredQuantity = promotion.get().getRequiredQuantity();
        int applicableSets = desiredQuantity / requiredQuantity;
        int applicableQuantity = applicableSets * requiredQuantity;
        int insufficientQuantity = desiredQuantity - applicableQuantity;

        if (insufficientQuantity > 0) {
            return String.format("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)", product.getName(), insufficientQuantity);
        }
        return "";
    }
}
