package store.parser;

import store.domain.Product;

public class ProductParser {

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
        return new Product(name, price, quantity, promotion);
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + "의 숫자 형식이 잘못되었습니다: " + value, e);
        }
    }
}
