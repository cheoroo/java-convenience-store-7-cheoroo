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

    public int calculateDiscount(Product product, int quantity) {
        return getApplicablePromotion(product)
                .filter(Promotion::isValidToday)
                .map(promotion -> calculateDiscountAmount(promotion, quantity, product.getPrice()))
                .orElse(0);
    }

    private int calculateDiscountAmount(Promotion promotion, int quantity, int pricePerUnit) {
        return promotion.calculateDiscount(quantity, pricePerUnit);
    }

    public String checkPromotionAvailability(Product product, int desiredQuantity) {
        return getApplicablePromotion(product)
                .filter(Promotion::isValidToday)
                .map(promotion -> buildAvailabilityMessage(promotion, product, desiredQuantity))
                .orElse("");
    }

    private String buildAvailabilityMessage(Promotion promotion, Product product, int desiredQuantity) {
        int requiredQuantity = promotion.getRequiredQuantity();
        if (desiredQuantity < requiredQuantity) {
            return formatAdditionalQuantityMessage(product, requiredQuantity - desiredQuantity);
        }
        if (desiredQuantity > product.getQuantity()) {
            return formatInsufficientStockMessage(product, desiredQuantity - product.getQuantity());
        }
        return "";
    }

    private String formatAdditionalQuantityMessage(Product product, int additionalQuantity) {
        return String.format("현재 %s은(는) %d개를 추가로 구매하면 혜택을 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                product.getName(), additionalQuantity);
    }

    private String formatInsufficientStockMessage(Product product, int unavailableQuantity) {
        return String.format("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                product.getName(), unavailableQuantity);
    }
}
