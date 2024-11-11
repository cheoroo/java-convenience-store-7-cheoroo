package store.service;

import store.domain.Product;
import store.domain.Promotion;
import store.domain.Receipt;
import store.repository.PromotionRepository;

import java.util.Map;
import java.util.Optional;

public class PurchaseService {
    private final PromotionRepository promotionRepository;

    public PurchaseService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Receipt generateReceipt(Map<Product, Integer> purchaseItems, boolean isMember) {
        Receipt receipt = new Receipt();
        int totalAmount = 0;
        int totalDiscount = 0;

        for (Map.Entry<Product, Integer> entry : purchaseItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            int amount = product.getPrice() * quantity;
            receipt.addItem(product.getName(), quantity, amount);
            totalAmount += amount;

            int discount = calculateDiscount(product, quantity, getApplicablePromotion(product));
            totalDiscount += discount;
        }

        int finalAmount = calculateFinalAmountWithDiscount(totalAmount, totalDiscount, isMember);
        receipt.setPromotionDiscount(totalDiscount);
        receipt.setMembershipDiscount(finalAmount - (totalAmount - totalDiscount));
        receipt.setFinalAmount(finalAmount);
        return receipt;
    }

    private int calculateTotalAmount(Map<Product, Integer> purchaseItems) {
        return purchaseItems.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getPrice())
                .sum();
    }

    private int calculateTotalDiscount(Map<Product, Integer> purchaseItems) {
        return purchaseItems.entrySet().stream()
                .mapToInt(entry -> calculateDiscount(entry.getKey(), entry.getValue(), getApplicablePromotion(entry.getKey())))
                .sum();
    }

    private Optional<Promotion> getApplicablePromotion(Product product) {
        return promotionRepository.getPromotions().stream()
                .filter(promotion -> promotion.getName().equals(product.getPromotion()))
                .findFirst();
    }

    private int calculateDiscount(Product product, int quantity, Optional<Promotion> promotion) {
        if (promotion.isEmpty() || !promotion.get().isValidToday()) {
            return 0;
        }
        return promotion.get().calculateDiscount(quantity, product.getPrice());
    }

    public String checkPromotionAvailability(Product productWithPromotion, Product productWithoutPromotion, int desiredQuantity) {
        validateStockAvailability(productWithPromotion, productWithoutPromotion, desiredQuantity);

        Optional<Promotion> promotion = getApplicablePromotion(productWithPromotion);
        if (!isPromotionApplicable(promotion)) {
            return "";
        }

        int maxEligibleForDiscount = calculateMaxEligibleForDiscount(productWithPromotion, desiredQuantity, promotion.get());
        return generateAvailabilityMessage(productWithPromotion, desiredQuantity, maxEligibleForDiscount);
    }

    private void validateStockAvailability(Product productWithPromotion, Product productWithoutPromotion, int desiredQuantity) {
        int totalStock = calculateTotalStock(productWithPromotion, productWithoutPromotion);
        if (desiredQuantity > totalStock) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private int calculateTotalStock(Product productWithPromotion, Product productWithoutPromotion) {
        return productWithPromotion.getQuantity() + productWithoutPromotion.getQuantity();
    }

    private boolean isPromotionApplicable(Optional<Promotion> promotion) {
        return promotion.isPresent() && promotion.get().isValidToday();
    }

    private int calculateMaxEligibleForDiscount(Product product, int desiredQuantity, Promotion promotion) {
        return Math.min(desiredQuantity, promotion.calculateEligibleDiscountUnits(product.getQuantity()));
    }

    private String generateAvailabilityMessage(Product product, int desiredQuantity, int maxEligibleForDiscount) {
        int undiscountedUnits = desiredQuantity - maxEligibleForDiscount;
        if (undiscountedUnits > 0) {
            return formatInsufficientStockMessage(product, undiscountedUnits);
        }
        return "";
    }

    private String formatInsufficientStockMessage(Product product, int unavailableQuantity) {
        return String.format("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                product.getName(), unavailableQuantity);
    }

    private int calculateFinalAmountWithDiscount(int totalAmount, int totalDiscount, boolean isMember) {
        int membershipDiscount = 0;
        if (isMember) {
            membershipDiscount = calculateMembershipDiscount(totalAmount - totalDiscount);
        }
        return totalAmount - totalDiscount - membershipDiscount;
    }

    private int calculateMembershipDiscount(int amount) {
        int discount = (int) (amount * 0.3);
        return Math.min(discount, 8000);
    }
}
