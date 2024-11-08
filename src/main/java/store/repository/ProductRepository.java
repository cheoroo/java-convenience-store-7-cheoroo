package store.repository;

import store.domain.Product;
import store.parser.ProductParser;
import store.utils.FileLoader;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();
    private final ProductParser productParser = new ProductParser();

    public ProductRepository() {
        loadProducts();
    }

    private void loadProducts() {
        FileLoader.loadFile("/products.md", this::processLine);
    }

    private void processLine(String line) {
        products.add(productParser.parseProduct(line));
    }

    public List<Product> getProducts() {
        return products;
    }
}
