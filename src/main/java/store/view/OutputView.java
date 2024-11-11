package store.view;

import store.domain.Product;
import store.domain.Receipt;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import store.domain.ReceiptItem;

public class OutputView {

    public void printWelcomeMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
    }

    public void printProducts(List<Product> products) {
        for (Product product : products) {
            System.out.println(formatProductDisplay(product));
        }
        System.out.println();
    }

    private String formatProductDisplay(Product product) {
        String price = formatPrice(product.getPrice());
        String quantity = getQuantityText(product);
        String promotion = getPromotionText(product);

        if (!promotion.isEmpty()) {
            return String.format("- %s %s원 %s %s", product.getName(), price, quantity, promotion);
        }
        return String.format("- %s %s원 %s", product.getName(), price, quantity);
    }

    private String getPromotionText(Product product) {
        return product.getPromotion() != null ? product.getPromotion() : "";
    }

    private String getQuantityText(Product product) {
        if (product.getQuantity() > 0) {
            return product.getQuantity() + "개";
        }
        return "재고 없음";
    }

    public void printReceipt(Receipt receipt) {
        printHeader();
        printItems(receipt.getItems());
        printFreeItems(receipt.getFreeItems());
        printTotals(receipt);
    }

    private void printHeader() {
        System.out.println("==============W 편의점================");
        System.out.println(String.format("%-12s %6s %10s", "상품명", "수량", "금액"));
    }

    private void printItems(List<ReceiptItem> items) {
        for (ReceiptItem item : items) {
            System.out.println(String.format("%-12s %6d %10s",
                    item.getName(),
                    item.getQuantity(),
                    formatPrice(item.getAmount())
            ));
        }
    }

    private void printFreeItems(List<ReceiptItem> freeItems) {
        System.out.println("=============증    정===============");
        for (ReceiptItem freeItem : freeItems) {
            System.out.println(String.format("%-12s %6d",
                    freeItem.getName(),
                    freeItem.getQuantity()
            ));
        }
    }

    private void printTotals(Receipt receipt) {
        System.out.println("====================================");
        System.out.println(String.format("%-12s %6d %10s", "총구매액",
                receipt.getTotalQuantity(), formatPrice(receipt.getTotalAmount())));
        System.out.println(String.format("%-12s %16s", "행사할인",
                "-" + formatPrice(receipt.getPromotionDiscount())));
        System.out.println(String.format("%-12s %16s", "멤버십할인",
                "-" + formatPrice(receipt.getMembershipDiscount())));
        System.out.println(String.format("%-12s %16s", "내실돈",
                formatPrice(receipt.getFinalAmount())));
    }

    public void printError(String message) {
        System.out.println(message);
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }
}
