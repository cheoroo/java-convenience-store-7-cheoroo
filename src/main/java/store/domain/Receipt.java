package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private final List<ReceiptItem> items = new ArrayList<>();
    private final List<ReceiptItem> freeItems = new ArrayList<>();
    private int totalAmount;
    private int totalQuantity;
    private int promotionDiscount;
    private int membershipDiscount;
    private int finalAmount;

    public void addItem(String name, int quantity, int amount) {
        items.add(new ReceiptItem(name, quantity, amount));
    }

    public void addFreeItem(String name, int quantity) {
        freeItems.add(new ReceiptItem(name, quantity, 0));
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public List<ReceiptItem> getFreeItems() {
        return freeItems;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setPromotionDiscount(int promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public void setMembershipDiscount(int membershipDiscount) {
        this.membershipDiscount = membershipDiscount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
}
