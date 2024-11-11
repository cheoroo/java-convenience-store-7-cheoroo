package store.domain;

public class ReceiptItem {
    private final String name;
    private final int quantity;
    private final int amount;

    public ReceiptItem(String name, int quantity, int amount) {
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getAmount() { return amount; }
}
