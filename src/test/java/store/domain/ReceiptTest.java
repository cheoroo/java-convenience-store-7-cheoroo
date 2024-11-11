package store.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReceiptTest {

    @Test
    void 영수증_생성_테스트() {
        Receipt receipt = new Receipt();
        receipt.addItem("콜라", 3, 3000);
        receipt.addItem("에너지바", 5, 10000);
        receipt.addFreeItem("콜라", 1);
        receipt.setPromotionDiscount(1000);
        receipt.setMembershipDiscount(3000);
        receipt.setFinalAmount(9000);

        String expectedReceipt = "==============W 편의점================\n" +
                "상품명              수량         금액\n" +
                "콜라                3      3,000\n" +
                "에너지바              5     10,000\n" +
                "=============증    정===============\n" +
                "콜라                1\n" +
                "====================================\n" +
                "총구매액              8     13,000\n" +
                "행사할인                   -1,000\n" +
                "멤버십할인                  -3,000\n" +
                "내실돈                     9,000\n";

        String display = receipt.getReceiptDisplay();

        assertThat(display).isEqualTo(expectedReceipt);
    }
}
