package store.repository;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Nested
    @DisplayName("재고 업데이트 테스트")
    class UpdateStockTest {

        @Test
        @DisplayName("재고를 정상적으로 업데이트한다")
        void updateStockSuccessfully() {
            List<Product> products = productRepository.getProducts();
            Product cola = products.stream()
                    .filter(p -> p.getName().equals("콜라") && p.getPromotion() == null)
                    .findFirst()
                    .orElseThrow();

            int initialQuantity = cola.getQuantity();
            int purchaseQuantity = 5;

            Map<Product, Integer> purchaseItems = new HashMap<>();
            purchaseItems.put(cola, purchaseQuantity);

            productRepository.updateStock(purchaseItems);

            assertThat(cola.getQuantity()).isEqualTo(initialQuantity - purchaseQuantity);
        }

        @Test
        @DisplayName("재고가 부족한 경우 예외를 발생시킨다")
        void throwExceptionWhenStockInsufficient() {
            List<Product> products = productRepository.getProducts();
            Product cola = products.stream()
                    .filter(p -> p.getName().equals("콜라") && p.getPromotion() == null)
                    .findFirst()
                    .orElseThrow();

            int purchaseQuantity = cola.getQuantity() + 1;

            Map<Product, Integer> purchaseItems = new HashMap<>();
            purchaseItems.put(cola, purchaseQuantity);

            assertThatThrownBy(() -> productRepository.updateStock(purchaseItems))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }
    }
}
