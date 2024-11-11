package store.domain;

import java.util.Objects;

public class Product {
    private String name;
    private int price;
    private int quantity;
    private String promotion;

    public Product(String name, int price, int quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public String getPromotion() {
        return promotion;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void reduceQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("차감할 수량은 음수가 될 수 없습니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
        this.quantity -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return price == product.price &&
                quantity == product.quantity &&
                Objects.equals(name, product.name) &&
                Objects.equals(promotion, product.promotion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, quantity, promotion);
    }
}
