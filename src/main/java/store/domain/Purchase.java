package store.domain;

import java.util.Map;

public class Purchase {
    private final Map<String, Integer> items;

    public Purchase(Map<String, Integer> items) {
        this.items = items;
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
