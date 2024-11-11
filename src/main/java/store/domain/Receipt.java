package store.domain;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Receipt {
    private final List<ReceiptItem> items = new ArrayList<>();
    private final List<ReceiptItem> freeItems = new ArrayList<>();
    private int totalAmount;
    private int totalQuantity;
    private int promotionDiscount;
    private int membershipDiscount;
    private int finalAmount;

    public void addItem(String name, int quantity, int amount) {
        items.add(new ReceiptItem(name, quantity, amount));
        totalQuantity += quantity;
        totalAmount += amount;
    }

    public void addFreeItem(String name, int quantity) {
        freeItems.add(new ReceiptItem(name, quantity, 0));
    }

    public void setPromotionDiscount(int promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public void setMembershipDiscount(int membershipDiscount) {
        this.membershipDiscount = membershipDiscount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getReceiptDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("==============W 편의점================\n");
        sb.append(String.format("%-12s %6s %10s\n", "상품명", "수량", "금액"));

        appendItems(sb);
        appendFreeItems(sb);
        appendTotals(sb);

        return sb.toString();
    }

    private void appendItems(StringBuilder sb) {
        for (ReceiptItem item : items) {
            sb.append(String.format("%-12s %6d %10s\n",
                    adjustKoreanWidth(item.getName(), 12),
                    item.getQuantity(),
                    formatPrice(item.getAmount())
            ));
        }
    }

    private void appendFreeItems(StringBuilder sb) {
        sb.append("=============증    정===============\n");
        for (ReceiptItem item : freeItems) {
            sb.append(String.format("%-12s %6d\n",
                    adjustKoreanWidth(item.getName(), 12),
                    item.getQuantity()
            ));
        }
    }

    private void appendTotals(StringBuilder sb) {
        sb.append("====================================\n");
        sb.append(String.format("%-12s %6d %10s\n", "총구매액", totalQuantity, formatPrice(totalAmount)));
        sb.append(String.format("%-12s %16s\n", "행사할인", "-" + formatPrice(promotionDiscount)));
        sb.append(String.format("%-12s %16s\n", "멤버십할인", "-" + formatPrice(membershipDiscount)));
        sb.append(String.format("%-12s %16s\n", "내실돈", formatPrice(finalAmount)));
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }

    private String adjustKoreanWidth(String text, int fieldWidth) {
        int koreanChars = countKoreanChars(text);
        int adjustedWidth = fieldWidth - koreanChars;
        return String.format("%-" + adjustedWidth + "s", text);
    }

    private int countKoreanChars(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            count += koreanCharCount(c);
        }
        return count;
    }

    private int koreanCharCount(char c) {
        if (isKorean(c)) {
            return 1;
        }
        return 0;
    }

    private boolean isKorean(char c) {
        return (c >= 0xAC00 && c <= 0xD7A3);
    }
}
