package store.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest {

    private PromotionRepository promotionRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        promotionRepository = new PromotionRepository();
        productRepository = new ProductRepository(promotionRepository);
    }

    @Nested
    @DisplayName("상품 데이터 로드 테스트")
    class LoadProductDataTest {

        @Test
        @DisplayName("상품 목록의 갯수를 정상적으로 불러온다")
        void loadProductsSuccessfully() {
            List<Product> products = productRepository.getProducts();
            assertThat(products).isNotEmpty();
            assertThat(products.size()).isEqualTo(18);
        }

        @Test
        @DisplayName("첫 번째 상품의 정보를 모두 정상적으로 불러온다")
        void loadFirstProductInformationSuccessfully() {
            List<Product> products = productRepository.getProducts();
            Product firstProduct = new Product("콜라", 1000, 10, "탄산2+1");
            assertThat(products.get(0)).isEqualTo(firstProduct);
        }

        @Test
        @DisplayName("재고가 없는 프로모션 상품이 추가되었는지 확인한다")
        void containsOutOfStockProducts() {
            List<Product> products = productRepository.getProducts();
            assertThat(products.stream().filter(p -> p.getQuantity() == 0)).hasSize(2);
        }
    }
}
