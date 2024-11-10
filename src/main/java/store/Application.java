package store;

import store.domain.Product;
import store.repository.ProductRepository;
import store.view.OutputView;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        List<Product> products = productRepository.getProducts();

        OutputView outputView = new OutputView();
        outputView.printWelcomeMessage();
        outputView.printProducts(products);
        System.out.println();
        outputView.printPurchasePrompt();
    }
}
