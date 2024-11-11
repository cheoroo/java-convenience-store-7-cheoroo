package store.domain;

public enum PromotionType {
    BUY_ONE_GET_ONE_FREE(1, 1),
    BUY_TWO_GET_ONE_FREE(2, 1);

    private final int buyQuantity;
    private final int freeQuantity;

    PromotionType(int buyQuantity, int freeQuantity) {
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    public int calculateDiscount(int quantity, int pricePerUnit) {
        int sets = quantity / (buyQuantity + freeQuantity);
        int freeItems = sets * freeQuantity;
        return freeItems * pricePerUnit;
    }

    public int getRequiredQuantity() {
        return buyQuantity + freeQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }
}
