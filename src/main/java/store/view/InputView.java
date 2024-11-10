package store.view;

import camp.nextstep.edu.missionutils.Console;
import store.domain.Purchase;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class InputView {

    public Purchase readPurchase() {
        String input = readInputLine();
        Map<String, Integer> items = parseInput(input);
        return new Purchase(items);
    }

    private String readInputLine() {
        try {
            return Console.readLine();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("[ERROR] 입력이 올바르지 않습니다.");
        }
    }

    private Map<String, Integer> parseInput(String input) {
        Map<String, Integer> items = new HashMap<>();
        String[] pairs = input.split("],\\[");

        for (String pair : pairs) {
            String[] parsedPair = parsePair(pair);
            items.put(parsedPair[0], Integer.parseInt(parsedPair[1]));
        }
        return items;
    }

    private String[] parsePair(String pair) {
        String cleanPair = pair.replaceAll("[\\[\\]]", "");
        String[] parts = cleanPair.split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식입니다.");
        }

        validateQuantity(parts[1]);
        return parts;
    }

    private void validateQuantity(String quantity) {
        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 수량은 숫자여야 합니다.");
        }
    }
}
