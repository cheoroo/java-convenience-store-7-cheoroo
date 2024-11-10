package store.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import store.domain.Product;
import store.utils.FileLoader;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();
    private final Map<String, List<Product>> productMap = new LinkedHashMap<>();
    private final PromotionRepository promotionRepository;

    public ProductRepository(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
        loadProducts();
        addMissingStockProducts();
    }

    private void loadProducts() {
        FileLoader.loadFile("/products.md", this::processLine);
    }

    private void processLine(String line) {
        Product product = createProduct(line.split(","));
        if (product.getPromotion() != null && !isValidPromotion(product.getPromotion())) {
            throw new IllegalArgumentException("유효하지 않은 프로모션 이름: " + product.getPromotion());
        }
        productMap.computeIfAbsent(product.getName(), k -> new ArrayList<>()).add(product);
    }

    private boolean isValidPromotion(String promotionName) {
        return promotionRepository.getPromotions().stream()
                .anyMatch(promotion -> promotion.getName().equals(promotionName));
    }

    private Product createProduct(String[] data) {
        return new Product(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), getPromotionValue(data));
    }

    private void addMissingStockProducts() {
        for (Map.Entry<String, List<Product>> entry : productMap.entrySet()) {
            List<Product> productList = entry.getValue();
            products.addAll(productList);
            addOutOfStockProductIfNeeded(productList);
        }
    }

    private void addOutOfStockProductIfNeeded(List<Product> productList) {
        if (requiresOutOfStockProduct(productList)) {
            products.add(createOutOfStockProduct(productList.get(0)));
        }
    }

    private boolean requiresOutOfStockProduct(List<Product> productList) {
        return productList.stream().anyMatch(p -> p.getPromotion() != null) &&
                productList.stream().noneMatch(p -> p.getPromotion() == null);
    }

    private Product createOutOfStockProduct(Product product) {
        return new Product(product.getName(), product.getPrice(), 0, null);
    }

    private String getPromotionValue(String[] data) {
        if (data.length > 3 && !data[3].equalsIgnoreCase("null")) {
            return data[3];
        }
        return null;
    }

    public void updateStock(Map<Product, Integer> purchaseItems) {
        for (Map.Entry<Product, Integer> entry : purchaseItems.entrySet()) {
            Product product = entry.getKey();
            int quantityPurchased = entry.getValue();

            product.reduceQuantity(quantityPurchased);
        }
    }

    public List<Product> getProducts() {
        return products;
    }
}
