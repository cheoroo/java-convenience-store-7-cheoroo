package store.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionTest {

    @Nested
    @DisplayName("프로모션 기간 검증")
    class PromotionValidityTests {

        @Test
        @DisplayName("오늘 날짜가 프로모션 기간 내에 포함된 경우, 프로모션을 적용할 수 있다")
        void applyPromotionWhenValidDate() {
            Promotion promotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE,
                    LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
            assertThat(promotion.isValidToday()).isTrue();
        }

        @Test
        @DisplayName("프로모션 기간이 지난 경우, 프로모션을 적용할 수 없다")
        void doNotApplyPromotionWhenInvalidDate() {
            Promotion promotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE,
                    LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
            assertThat(promotion.isValidToday()).isFalse();
        }
    }

    @Nested
    @DisplayName("할인 금액 계산")
    class DiscountCalculationTests {

        @Test
        @DisplayName("N+1 프로모션에서 할인된 가격이 올바르게 계산된다")
        void calculateDiscountCorrectlyForNPlusOnePromotion() {
            Promotion promotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE,
                    LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
            int quantity = 6;
            int pricePerUnit = 1000;

            int discount = promotion.calculateDiscount(quantity, pricePerUnit);
            assertThat(discount).isEqualTo(2000);
        }

        @Test
        @DisplayName("1+1 프로모션에서 할인된 가격이 올바르게 계산된다")
        void calculateDiscountCorrectlyForBuyOneGetOneFreePromotion() {
            Promotion promotion = new Promotion("탄산1+1", PromotionType.BUY_ONE_GET_ONE_FREE,
                    LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
            int quantity = 4;
            int pricePerUnit = 1000;

            int discount = promotion.calculateDiscount(quantity, pricePerUnit);
            assertThat(discount).isEqualTo(2000);
        }

        @Test
        @DisplayName("적용 가능한 프로모션이 없는 경우 할인 금액은 0")
        void noDiscountWhenNoValidPromotion() {
            Promotion invalidPromotion = new Promotion("만료된 프로모션", PromotionType.BUY_TWO_GET_ONE_FREE,
                    LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
            int quantity = 5;
            int pricePerUnit = 1000;

            int discount = invalidPromotion.calculateDiscount(quantity, pricePerUnit);
            assertThat(discount).isEqualTo(0);
        }
    }
}
