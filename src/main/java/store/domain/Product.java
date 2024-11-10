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
