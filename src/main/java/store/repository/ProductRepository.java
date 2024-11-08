package store.repository;

import store.domain.Product;
import store.utils.FileLoader;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public ProductRepository() {
        loadProducts();
    }

    private void loadProducts() {
        FileLoader.loadFile("/products.md", this::processLine);
    }

    private void processLine(String line) {
        String[] data = line.split(",");
        String name = data[0];
        int price = Integer.parseInt(data[1]);
        int quantity = Integer.parseInt(data[2]);
        String promotion = getPromotionValue(data);

        products.add(new Product(name, price, quantity, promotion));
    }

    private String getPromotionValue(String[] data) {
        if (data.length > 3) {
            return data[3];
        }
        return null;
    }

    public List<Product> getProducts() {
        return products;
    }
}
