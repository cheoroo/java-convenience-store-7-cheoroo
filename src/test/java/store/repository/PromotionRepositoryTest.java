package store.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Promotion;
import store.domain.PromotionType;

class PromotionRepositoryTest {

    private final PromotionRepository promotionRepository = new PromotionRepository();

    @Nested
    @DisplayName("프로모션 데이터 로드 테스트")
    class LoadPromotionDataTest{

        @Test
        @DisplayName("프로모션 데이터 갯수 정보를 올바르게 불러온다")
        void loadPromotionsSuccessfully() {
            List<Promotion> promotions = promotionRepository.getPromotions();
            assertThat(promotions).isNotEmpty();
            assertThat(promotions.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("첫 번째 프로모션의 정보를 모두 정상적으로 불러온다")
        void loadFirstPromotionInfomationSuccessfully() {
            List<Promotion> promotions = promotionRepository.getPromotions();
            Promotion firstPromotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            assertThat(promotions.get(0)).isEqualTo(firstPromotion);
        }
    }
}