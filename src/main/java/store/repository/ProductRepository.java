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

    public ProductRepository() {
        loadProducts();
        addMissingStockProducts();
    }

    private void loadProducts() {
        FileLoader.loadFile("/products.md", this::processLine);
    }

    private void processLine(String line) {
        Product product = createProduct(line.split(","));
        productMap.computeIfAbsent(product.getName(), k -> new ArrayList<>()).add(product);
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

    public List<Product> getProducts() {
        return products;
    }
}
