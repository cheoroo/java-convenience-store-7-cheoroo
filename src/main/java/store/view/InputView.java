package store.view;

import camp.nextstep.edu.missionutils.Console;
import store.domain.Purchase;
import store.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class InputView {
    private final ProductRepository productRepository;

    public InputView(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Purchase readPurchase() {
        while (true) {
            try {
                String input = readInputLine("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
                return new Purchase(parseInput(input));
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("재고 수량을 초과")) {
                    System.out.println(e.getMessage());
                    break;
                } else {
                    System.out.println(e.getMessage());
                }
            }
        }
        return null;
    }

    public boolean askForAdditionalPurchase() {
        return confirmInput("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
    }

    public boolean askForMembershipDiscount() {
        return confirmInput("멤버십 할인을 받으시겠습니까? (Y/N)");
    }

    public boolean askToAddFreeItems(String message) {
        return confirmInput(message);
    }

    public boolean askToPayForItemsWithoutDiscount(String message) {
        return confirmInput(message);
    }

    private String readInputLine(String prompt) {
        System.out.println(prompt);
        return readLineWithErrorHandling();
    }

    private String readLineWithErrorHandling() {
        try {
            return Console.readLine();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("[ERROR] 입력이 올바르지 않습니다.");
        }
    }

    private Map<String, Integer> parseInput(String input) {
        Map<String, Integer> items = new HashMap<>();
        for (String pair : input.split("],\\[")) {
            String[] parsedPair = parsePair(pair);
            addItemToMap(items, parsedPair[0], Integer.parseInt(parsedPair[1]));
        }
        return items;
    }

    private void addItemToMap(Map<String, Integer> items, String productName, int quantity) {
        validateProduct(productName, quantity);
        items.put(productName, quantity);
    }

    private String[] parsePair(String pair) {
        String[] parts = cleanAndSplitPair(pair);
        validateQuantity(parts[1]);
        return parts;
    }

    private String[] cleanAndSplitPair(String pair) {
        String cleanPair = pair.replaceAll("[\\[\\]]", "");
        String[] parts = cleanPair.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
        return parts;
    }

    private void validateProduct(String productName, int quantity) {
        if (!productRepository.exists(productName)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        productRepository.hasSufficientStock(productName, quantity);
    }

    private void validateQuantity(String quantity) {
        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 수량은 숫자여야 합니다. 다시 입력해 주세요.");
        }
    }

    private boolean confirmInput(String message) {
        while (true) {
            String input = readInputLine(message);
            if ("Y".equalsIgnoreCase(input)) return true;
            if ("N".equalsIgnoreCase(input)) return false;
            System.out.println("[ERROR] 입력이 올바르지 않습니다.");
        }
    }
}
