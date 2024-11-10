package store.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.repository.ProductRepository;

import java.util.List;
import store.repository.PromotionRepository;

import static org.assertj.core.api.Assertions.assertThat;

class OutputViewTest {

    private final OutputView outputView = new OutputView();
    private PromotionRepository promotionRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        promotionRepository = new PromotionRepository();
        productRepository = new ProductRepository(promotionRepository);
    }

    @Nested
    @DisplayName("상품 목록 출력 테스트")
    class ProductListDisplayTest {

        @Test
        @DisplayName("상품 목록이 올바르게 포맷되고 순서가 유지되어 출력된다")
        void displayProductListCorrectly() {
            List<Product> products = productRepository.getProducts();
            String display = outputView.getProductListDisplay(products);

            String[] expectedLines = {
                    "- 콜라 1,000원 10개 탄산2+1",
                    "- 콜라 1,000원 10개",
                    "- 사이다 1,000원 8개 탄산2+1",
                    "- 사이다 1,000원 7개",
                    "- 오렌지주스 1,800원 9개 MD추천상품",
                    "- 오렌지주스 1,800원 재고 없음",
                    "- 탄산수 1,200원 5개 탄산2+1",
                    "- 탄산수 1,200원 재고 없음",
                    "- 물 500원 10개",
                    "- 비타민워터 1,500원 6개",
                    "- 감자칩 1,500원 5개 반짝할인",
                    "- 감자칩 1,500원 5개",
                    "- 초코바 1,200원 5개 MD추천상품",
                    "- 초코바 1,200원 5개",
                    "- 에너지바 2,000원 5개",
                    "- 정식도시락 6,400원 8개",
                    "- 컵라면 1,700원 1개 MD추천상품",
                    "- 컵라면 1,700원 10개"
            };

            String[] actualLines = display.split("\n");

            assertThat(actualLines).containsExactly(expectedLines);
        }
    }
}
