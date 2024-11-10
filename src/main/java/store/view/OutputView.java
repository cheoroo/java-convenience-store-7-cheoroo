package store.view;

import store.domain.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OutputView {

    public void printWelcomeMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
    }

    public void printPurchasePrompt() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
    }

    public void printProducts(List<Product> products) {
        System.out.print(getProductListDisplay(products));
    }

    public String getProductListDisplay(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(formatProductDisplay(product)).append("\n");
        }
        return sb.toString();
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

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }
}
