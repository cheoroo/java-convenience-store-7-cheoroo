package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.domain.Promotion;
import store.repository.PromotionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionServiceTest {

    private PromotionRepository promotionRepository;
    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        promotionRepository = new PromotionRepository();
        promotionService = new PromotionService(promotionRepository);
    }

    @Nested
    @DisplayName("할인 금액 계산 테스트")
    class CalculateDiscountTests {

        @Test
        @DisplayName("적용 가능한 프로모션이 있을 때 할인 금액을 올바르게 계산한다")
        void calculateDiscountWithValidPromotion() {
            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            Optional<Promotion> promotion = promotionService.getApplicablePromotion(product);

            int discount = promotion.map(p -> p.calculateDiscount(6, product.getPrice())).orElse(0);
            assertThat(discount).isEqualTo(2000);
        }

        @Test
        @DisplayName("유효한 프로모션이 없을 때 할인 금액은 0")
        void calculateDiscountWithNoValidPromotion() {
            Product product = new Product("콜라", 1000, 10, "없는 프로모션");
            Optional<Promotion> promotion = promotionService.getApplicablePromotion(product);

            int discount = promotion.map(p -> p.calculateDiscount(5, product.getPrice())).orElse(0);
            assertThat(discount).isEqualTo(0);
        }

        @Test
        @DisplayName("N+1 프로모션에서 N의 배수가 아닌 수량으로 요청한 경우, 정확한 할인을 반환한다")
        void calculateDiscountForNonMultipleQuantityInPromotion() {
            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            Optional<Promotion> promotion = promotionService.getApplicablePromotion(product);

            int discount = promotion.map(p -> p.calculateDiscount(7, product.getPrice())).orElse(0);
            assertThat(discount).isEqualTo(2000);
        }
    }
}
