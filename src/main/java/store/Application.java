package store;

import store.domain.Product;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;
import store.view.OutputView;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        PromotionRepository promotionRepository = new PromotionRepository();
        ProductRepository productRepository = new ProductRepository(promotionRepository);
        List<Product> products = productRepository.getProducts();

        OutputView outputView = new OutputView();
        outputView.printWelcomeMessage();
        outputView.printProducts(products);
        System.out.println();
        outputView.printPurchasePrompt();
    }
}
