package store.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest {

    private final ProductRepository productRepository = new ProductRepository();

    @Nested
    @DisplayName("상품 데이터 로드 테스트")
    class LoadProductDataTest {

        @Test
        @DisplayName("상품 목록의 갯수를 정상적으로 불러온다")
        void loadProductsSuccessfully() {
            List<Product> products = productRepository.getProducts();
            assertThat(products).isNotEmpty();
            assertThat(products.size()).isEqualTo(16);
        }

        @Test
        @DisplayName("첫 번째 상품의 정보를 모두 정상적으로 불러온다")
        void loadFirstProductInfomationSuccessfully() {
            List<Product> products = productRepository.getProducts();
            Product firstProduct = new Product("콜라", 1000, 10, "탄산2+1");
            assertThat(products.get(0)).isEqualTo(firstProduct);
        }
    }
}
