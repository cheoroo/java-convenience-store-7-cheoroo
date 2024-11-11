package store.controller;

import store.domain.Product;
import store.domain.Purchase;
import store.domain.Receipt;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;
import store.service.PromotionService;
import store.service.PurchaseService;
import store.view.InputView;
import store.view.OutputView;

import java.util.HashMap;
import java.util.Map;

public class StoreController {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;
    private final PurchaseService purchaseService;
    private final InputView inputView;
    private final OutputView outputView;

    public StoreController() {
        promotionRepository = new PromotionRepository();
        productRepository = new ProductRepository(promotionRepository);
        promotionService = new PromotionService(promotionRepository);
        purchaseService = new PurchaseService(promotionRepository);
        inputView = new InputView(productRepository);
        outputView = new OutputView();
    }

    public void run() {
        outputView.printWelcomeMessage();
        outputView.printProducts(productRepository.getProducts());
        Map<Product, Integer> purchaseItems = new HashMap<>();
        processPurchases(purchaseItems);
        boolean isMember = inputView.askForMembershipDiscount();
        Receipt receipt = purchaseService.generateReceipt(purchaseItems, isMember);
        outputView.printReceipt(receipt);
        outputView.printGoodbyeMessage();
    }

    private void processPurchases(Map<Product, Integer> purchaseItems) {
        do {
            Purchase purchase = inputView.readPurchase();
            handlePurchase(purchase, purchaseItems);
        } while (inputView.askForAdditionalPurchase());
    }

    private void handlePurchase(Purchase purchase, Map<Product, Integer> purchaseItems) {
        for (Map.Entry<String, Integer> entry : purchase.getItems().entrySet()) {
            Product product = productRepository.getProductByName(entry.getKey());
            int quantity = entry.getValue();
            String message = promotionService.checkPromotionAvailability(product, quantity);
            if (!message.isEmpty() && !inputView.askForConfirmation(message)) {
                continue;
            }
            product.reduceQuantity(quantity);
            purchaseItems.put(product, quantity);
        }
    }
}
