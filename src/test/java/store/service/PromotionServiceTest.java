package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.PromotionType;
import store.repository.PromotionRepository;

import java.time.LocalDate;

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
            int discount = promotionService.calculateDiscount(product, 6);
            assertThat(discount).isEqualTo(2000);
        }

        @Test
        @DisplayName("유효한 프로모션이 없을 때 할인 금액은 0")
        void calculateDiscountWithNoValidPromotion() {
            Product product = new Product("콜라", 1000, 10, "없는 프로모션");
            int discount = promotionService.calculateDiscount(product, 5);
            assertThat(discount).isEqualTo(0);
        }

        @Test
        @DisplayName("N+1 프로모션에서 N의 배수가 아닌 수량으로 요청한 경우, 정확한 할인을 반환한다")
        void calculateDiscountForNonMultipleQuantityInPromotion() {
            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            int discount = promotionService.calculateDiscount(product, 7);
            assertThat(discount).isEqualTo(2000);
        }
    }

    @Nested
    @DisplayName("프로모션 조건 및 재고 메시지 테스트")
    class PromotionAvailabilityMessageTests {

        @Test
        @DisplayName("프로모션 조건에 도달하지 못한 경우 알림 메시지를 반환한다")
        void checkPromotionAvailabilityWhenQuantityNotEnough() {
            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            String message = promotionService.checkPromotionAvailability(product, 1);
            assertThat(message).isEqualTo("현재 콜라은(는) 2개를 추가로 구매하면 혜택을 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
        }

        @Test
        @DisplayName("재고 부족으로 일부 수량에 대한 프로모션 할인 적용 불가 시 알림 메시지를 반환한다")
        void checkPromotionAvailabilityWhenStockIsInsufficient() {
            Product product = new Product("콜라", 1000, 5, "탄산2+1");
            String message = promotionService.checkPromotionAvailability(product, 7);
            assertThat(message).isEqualTo("현재 콜라 2개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
        }

        @Test
        @DisplayName("유효한 프로모션이 없는 경우 빈 메시지를 반환한다")
        void checkPromotionAvailabilityWhenNoPromotion() {
            Product product = new Product("콜라", 1000, 10, "없는 프로모션");
            String message = promotionService.checkPromotionAvailability(product, 5);
            assertThat(message).isEmpty();
        }
    }

    @Nested
    @DisplayName("프로모션 경계 조건 및 예외 테스트")
    class PromotionEdgeCaseTests {

        @Test
        @DisplayName("요구된 수량을 정확히 충족했을 때, 추가 수량 요청 메시지가 없는지 확인한다")
        void noAdditionalQuantityMessageWhenExactRequiredQuantity() {
            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            String message = promotionService.checkPromotionAvailability(product, 3);
            assertThat(message).isEmpty();
        }

        @Test
        @DisplayName("오늘 날짜가 프로모션 기간에 포함되지 않을 때, 프로모션이 적용되지 않는다")
        void noPromotionAppliedWhenDateIsInvalid() {
            promotionRepository.getPromotions().clear();
            Promotion expiredPromotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE,
                    LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
            promotionRepository.getPromotions().add(expiredPromotion);

            Product product = new Product("콜라", 1000, 10, "탄산2+1");
            int discount = promotionService.calculateDiscount(product, 6);
            assertThat(discount).isEqualTo(0);
        }
    }
}
