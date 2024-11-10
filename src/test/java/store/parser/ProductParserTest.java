package store.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.repository.PromotionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductParserTest {

    private final PromotionRepository promotionRepository = new PromotionRepository();
    private final ProductParser parser = new ProductParser(promotionRepository);

    @Nested
    @DisplayName("Product 객체 생성 테스트")
    class ProductCreationTest {

        @Test
        @DisplayName("올바른 데이터로 Product 객체 생성")
        void parseProductSuccessfully() {
            String line = "콜라,1000,10,탄산2+1";
            Product expectedProduct = new Product("콜라", 1000, 10, "탄산2+1");
            Product parsedProduct = parser.parseProduct(line);

            assertThat(parsedProduct).isEqualTo(expectedProduct);
        }
    }

    @Nested
    @DisplayName("예외 발생 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("잘못된 숫자 형식으로 인해 예외가 발생한다")
        void throwsExceptionWhenPriceIsNotNumber() {
            String line = "콜라,abc,10";
            assertThatThrownBy(() -> parser.parseProduct(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 가격의 숫자 형식이 잘못되었습니다");
        }

        @Test
        @DisplayName("데이터 필드가 부족할 때 예외가 발생한다")
        void throwsExceptionWhenDataFieldsAreMissing() {
            String line = "콜라,1000"; // quantity 누락
            assertThatThrownBy(() -> parser.parseProduct(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 상품 데이터 형식입니다");
        }
        @Test
        @DisplayName("유효하지 않은 프로모션 이름이 포함된 상품 데이터 로드시 예외가 발생한다")
        void throwsExceptionWhenInvalidPromotionName() {
            String invalidProductLine = "콜라,1000,10,유효하지않은프로모션";

            assertThatThrownBy(() -> parser.parseProduct(invalidProductLine))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 프로모션 이름");
        }

    }
}
