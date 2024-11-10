package store.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Promotion;

import java.time.LocalDate;
import store.domain.PromotionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromotionParserTest {

    private final PromotionParser parser = new PromotionParser();

    @Nested
    @DisplayName("Promotion 객체 생성 테스트")
    class PromotionCreationTest {

        @Test
        @DisplayName("올바른 데이터로 Promotion 객체 생성")
        void parsePromotionSuccessfully() {
            String line = "탄산2+1,2,1,2024-01-01,2024-12-31";
            Promotion expectedPromotion = new Promotion("탄산2+1", PromotionType.BUY_TWO_GET_ONE_FREE, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            Promotion parsedPromotion = parser.parsePromotion(line);

            assertThat(parsedPromotion).isEqualTo(expectedPromotion);
        }
    }

    @Nested
    @DisplayName("예외 발생 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("잘못된 숫자 형식으로 인해 예외가 발생한다")
        void throwsExceptionWhenBuyQuantityIsNotNumber() {
            String line = "탄산2+1,abc,1,2024-01-01,2024-12-31";
            assertThatThrownBy(() -> parser.parsePromotion(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("프로모션 구매 수량의 숫자 형식이 잘못되었습니다");
        }

        @Test
        @DisplayName("잘못된 날짜 형식으로 인해 예외가 발생한다")
        void throwsExceptionWhenStartDateIsInvalid() {
            String line = "탄산2+1,2,1,invalid-date,2024-12-31";
            assertThatThrownBy(() -> parser.parsePromotion(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("프로모션 시작일의 날짜 형식이 잘못되었습니다");
        }

        @Test
        @DisplayName("데이터 필드가 부족할 때 예외가 발생한다")
        void throwsExceptionWhenDataFieldsAreMissing() {
            String line = "탄산2+1,2,1,2024-01-01"; // endDate 누락
            assertThatThrownBy(() -> parser.parsePromotion(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 프로모션 데이터 형식입니다");
        }

        @Test
        @DisplayName("지원하지 않는 프로모션 타입으로 인해 예외가 발생한다")
        void throwsExceptionForUnsupportedPromotionType() {
            String line = "탄산3+2,3,2,2024-01-01,2024-12-31";
            assertThatThrownBy(() -> parser.parsePromotion(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("지원하지 않는 프로모션 타입입니다");
        }

    }
}
