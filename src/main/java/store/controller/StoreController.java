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
        promotionService = new PromotionService(promotionRepository);
        productRepository = new ProductRepository(promotionRepository);
        purchaseService = new PurchaseService(promotionRepository, promotionService);
        inputView = new InputView(productRepository);
        outputView = new OutputView();
    }

    public void run() {
        outputView.printWelcomeMessage();
        outputView.printProducts(productRepository.getProducts());
        Map<Product, Integer> purchaseItems = new HashMap<>();

        boolean isShopping = true;
        while (isShopping) {
            processPurchases(purchaseItems);
            isShopping = inputView.askForAdditionalPurchase();
        }

        boolean isMember = inputView.askForMembershipDiscount();
        Receipt receipt = purchaseService.generateReceipt(purchaseItems, isMember);
        outputView.printReceipt(receipt);
    }

    private void processPurchases(Map<Product, Integer> purchaseItems) {
        Purchase purchase = inputView.readPurchase();
        if (purchase != null) {
            handlePurchase(purchase, purchaseItems);
        }
    }

    private void handlePurchase(Purchase purchase, Map<Product, Integer> purchaseItems) {
        for (Map.Entry<String, Integer> entry : purchase.getItems().entrySet()) {
            try {
                Product product = productRepository.getProductByName(entry.getKey());
                int quantity = entry.getValue();

                if (!productRepository.hasSufficientStock(product.getName(), quantity)) {
                    throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
                }

                String addFreeMessage = promotionService.checkPromotionAddFreeAvailability(product, quantity);
                if (!addFreeMessage.isEmpty()) {
                    boolean addFree = inputView.askToAddFreeItems(addFreeMessage);
                    if (addFree) {
                        int freeQuantity = promotionService.calculateFreeQuantity(quantity, product);
                        purchaseItems.merge(product, freeQuantity, Integer::sum);
                        product.reduceQuantity(freeQuantity);
                    }
                }

                String insufficientDiscountMessage = promotionService.checkPromotionInsufficientDiscountAvailability(product, quantity);
                if (!insufficientDiscountMessage.isEmpty()) {
                    boolean confirm = inputView.askToPayForItemsWithoutDiscount(insufficientDiscountMessage);
                    if (!confirm) {
                        continue;
                    }
                }

                product.reduceQuantity(quantity);
                purchaseItems.merge(product, quantity, Integer::sum);
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }
}
