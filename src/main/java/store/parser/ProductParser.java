package store.parser;

import store.domain.Product;
import store.repository.PromotionRepository;

public class ProductParser {
    private final PromotionRepository promotionRepository;

    public ProductParser(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Product parseProduct(String line) {
        String[] data = validateProductData(line);
        return createProduct(data);
    }

    private String[] validateProductData(String line) {
        String[] data = line.split(",");
        if (data.length < 3) {
            throw new IllegalArgumentException("잘못된 상품 데이터 형식입니다: " + line);
        }
        return data;
    }

    private Product createProduct(String[] data) {
        String name = data[0];
        int price = parseInteger(data[1], "상품 가격");
        int quantity = parseInteger(data[2], "상품 수량");
        String promotion = data.length > 3 ? data[3] : null;

        if (promotion != null && !isValidPromotion(promotion)) {
            throw new IllegalArgumentException("유효하지 않은 프로모션 이름: " + promotion);
        }

        return new Product(name, price, quantity, promotion);
    }

    private boolean isValidPromotion(String promotionName) {
        return promotionRepository.getPromotions().stream()
                .anyMatch(promotion -> promotion.getName().equals(promotionName));
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + "의 숫자 형식이 잘못되었습니다: " + value, e);
        }
    }
}
