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
        Totals totals = calculateTotals(purchaseItems, receipt);

        int membershipDiscount = calculateMembershipDiscount(totals.totalAmount - totals.totalDiscount, isMember);
        int finalAmount = totals.totalAmount - totals.totalDiscount - membershipDiscount;

        setReceiptTotals(receipt, totals, membershipDiscount, finalAmount);

        return receipt;
    }

    private Totals calculateTotals(Map<Product, Integer> purchaseItems, Receipt receipt) {
        Totals totals = new Totals();
        LocalDate currentDate = DateTimes.now().toLocalDate();

        for (Map.Entry<Product, Integer> entry : purchaseItems.entrySet()) {
            processReceiptItem(entry, receipt, totals, currentDate);
        }
        return totals;
    }

    private void processReceiptItem(Map.Entry<Product, Integer> entry, Receipt receipt, Totals totals, LocalDate currentDate) {
        Product product = entry.getKey();
        int quantity = entry.getValue();
        int amount = product.getPrice() * quantity;

        receipt.addItem(product.getName(), quantity, amount);
        totals.totalAmount += amount;
        totals.totalQuantity += quantity;

        int discount = calculateDiscount(product, quantity, receipt, currentDate);
        totals.totalDiscount += discount;
    }

    private int calculateDiscount(Product product, int quantity, Receipt receipt, LocalDate currentDate) {
        Optional<Promotion> promotion = promotionService.getApplicablePromotion(product);
        if (promotion.isPresent() && promotion.get().isValidOn(currentDate)) {
            int freeQuantity = promotionService.calculateFreeQuantity(quantity, product);
            addFreeItemsToReceipt(receipt, product, freeQuantity);
            return promotion.get().calculateDiscount(quantity, product.getPrice());
        }
        return 0;
    }

    private void addFreeItemsToReceipt(Receipt receipt, Product product, int freeQuantity) {
        if (freeQuantity > 0) {
            receipt.addFreeItem(product.getName(), freeQuantity);
        }
    }

    private int calculateMembershipDiscount(int amount, boolean isMember) {
        if (!isMember) {
            return 0;
        }
        int discount = (int) (amount * 0.3);
        return Math.min(discount, 8000);
    }

    private void setReceiptTotals(Receipt receipt, Totals totals, int membershipDiscount, int finalAmount) {
        receipt.setTotalAmount(totals.totalAmount);
        receipt.setTotalQuantity(totals.totalQuantity);
        receipt.setPromotionDiscount(totals.totalDiscount);
        receipt.setMembershipDiscount(membershipDiscount);
        receipt.setFinalAmount(finalAmount);
    }

    private static class Totals {
        int totalAmount = 0;
        int totalQuantity = 0;
        int totalDiscount = 0;
    }

    private int calculateMembershipDiscount(int amount) {
        int discount = (int) (amount * 0.3);
        return Math.min(discount, 8000);
    }
}
