package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.Receipt;
import store.repository.PromotionRepository;

import java.util.Map;
import java.util.Optional;

public class PurchaseService {
    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;

    public PurchaseService(PromotionRepository promotionRepository, PromotionService promotionService) {
        this.promotionRepository = promotionRepository;
        this.promotionService = promotionService;
    }

    public Receipt generateReceipt(Map<Product, Integer> purchaseItems, boolean isMember) {
        Receipt receipt = new Receipt();
        int totalAmount = 0;
        int totalQuantity = 0;
        int totalDiscount = 0;

        LocalDate currentDate = DateTimes.now().toLocalDate();

        for (Map.Entry<Product, Integer> entry : purchaseItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            int amount = product.getPrice() * quantity;

            receipt.addItem(product.getName(), quantity, amount);
            totalAmount += amount;
            totalQuantity += quantity;

            Optional<Promotion> promotion = promotionService.getApplicablePromotion(product);
            if (promotion.isPresent() && promotion.get().isValidOn(currentDate)) {
                int freeQuantity = promotionService.calculateFreeQuantity(quantity, product);
                if (freeQuantity > 0) {
                    receipt.addFreeItem(product.getName(), freeQuantity);
                }
                int discount = promotion.get().calculateDiscount(quantity, product.getPrice());
                totalDiscount += discount;
            }
        }

        int membershipDiscount = 0;
        if (isMember) {
            membershipDiscount = calculateMembershipDiscount(totalAmount - totalDiscount);
        }
        int finalAmount = totalAmount - totalDiscount - membershipDiscount;

        receipt.setTotalAmount(totalAmount);
        receipt.setTotalQuantity(totalQuantity);
        receipt.setPromotionDiscount(totalDiscount);
        receipt.setMembershipDiscount(membershipDiscount);
        receipt.setFinalAmount(finalAmount);

        return receipt;
    }

    private int calculateMembershipDiscount(int amount) {
        int discount = (int) (amount * 0.3);
        return Math.min(discount, 8000);
    }
}
