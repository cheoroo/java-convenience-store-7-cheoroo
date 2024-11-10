package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.repository.PromotionRepository;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PurchaseServiceTest {

    private PromotionRepository promotionRepository;
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        promotionRepository = new PromotionRepository();
        purchaseService = new PurchaseService(promotionRepository);
    }

    @Nested
    @DisplayName("혼재된 재고에서 프로모션 할인이 부분적으로 적용되는 경우")
    class MixedStockPromotionTests {

        @Test
        @DisplayName("프로모션 재고와 일반 재고 혼재 시 일부 할인이 적용되지 않은 경우 알림 메시지를 반환한다")
        void insufficientStockForFullPromotion() {
            Product productWithPromotion = new Product("콜라", 1000, 7, "탄산2+1");
            Product productWithoutPromotion = new Product("콜라", 1000, 10, null);

            Map<Product, Integer> purchaseItems = new LinkedHashMap<>();
            purchaseItems.put(productWithPromotion, 7);
            purchaseItems.put(productWithoutPromotion, 10);

            String message = purchaseService.checkPromotionAvailability(productWithPromotion, productWithoutPromotion, 10);
            assertThat(message).isEqualTo("현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
        }
    }

    @Nested
    @DisplayName("멤버십 할인 테스트")
    class MembershipDiscountTests {

        @Test
        @DisplayName("멤버십 할인이 올바르게 적용되는지 확인한다")
        void appliesMembershipDiscountCorrectly() {
            Product productWithPromotion = new Product("콜라", 1000, 10, "탄산2+1");

            Map<Product, Integer> purchaseItems = new LinkedHashMap<>();
            purchaseItems.put(productWithPromotion, 10);

            int finalAmount = purchaseService.processPurchase(purchaseItems, true);
            assertThat(finalAmount).isEqualTo(4900);
        }

        @Test
        @DisplayName("멤버십 할인은 최대 한도(8000원)를 초과하지 않는다")
        void appliesMembershipDiscountWithLimit() {
            Product productWithPromotion = new Product("콜라", 10000, 10, "탄산2+1");

            Map<Product, Integer> purchaseItems = new LinkedHashMap<>();
            purchaseItems.put(productWithPromotion, 10);

            int finalAmount = purchaseService.processPurchase(purchaseItems, true);
            assertThat(finalAmount).isEqualTo(62000); //70,000원 * 30% = 21,000원 이지만 min(21,000원, 8,000원) = 8,000원
        }
    }

    @Nested
    @DisplayName("재고가 충분하지 않아 모든 재고가 소진된 경우 테스트")
    class OutOfStockTests {

        @Test
        @DisplayName("재고 초과 시 예외 메시지를 반환한다")
        void throwsExceptionWhenQuantityExceedsStock() {
            Product productWithPromotion = new Product("사이다", 1000, 3, "탄산2+1");
            Product productWithoutPromotion = new Product("사이다", 1000, 0, null);

            Map<Product, Integer> purchaseItems = new LinkedHashMap<>();
            purchaseItems.put(productWithPromotion, 5);

            assertThatThrownBy(() -> purchaseService.checkPromotionAvailability(productWithPromotion, productWithoutPromotion, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }
}