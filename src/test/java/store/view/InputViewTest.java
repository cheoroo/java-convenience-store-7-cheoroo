package store.view;

import camp.nextstep.edu.missionutils.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Purchase;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputViewTest {

    private final PromotionRepository promotionRepository = new PromotionRepository();
    private final ProductRepository productRepository = new ProductRepository(promotionRepository);
    private final InputView inputView = new InputView(productRepository);

    @Nested
    @DisplayName("구매 항목 입력 테스트")
    class PurchaseInputTest {

        @AfterEach
        void resetConsole() {
            Console.close();
        }

        @Test
        @DisplayName("올바른 입력으로 Purchase 객체를 성공적으로 생성한다")
        void parsePurchaseInputSuccessfully() {
            String input = "[콜라-3],[사이다-2]";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            Purchase purchase = inputView.readPurchase();

            assertThat(purchase.getItems()).hasSize(2);
            assertThat(purchase.getItems().get("콜라")).isEqualTo(3);
        }

        @Test
        @DisplayName("잘못된 형식의 입력일 경우 예외를 발생시킨다")
        void throwsExceptionForInvalidFormat() {
            String input = "콜라-3,사이다-2";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        @Test
        @DisplayName("수량이 숫자가 아닌 경우 예외를 발생시킨다")
        void throwsExceptionForNonNumericQuantity() {
            String input = "[콜라-abc],[사이다-2]";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 수량은 숫자여야 합니다.");
        }

        @Test
        @DisplayName("존재하지 않는 상품을 입력했을 경우 예외를 발생시킨다")
        void throwsExceptionForNonExistingProduct() {
            String input = "[없는상품-3]";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }

        @Test
        @DisplayName("구매 수량이 재고 수량을 초과한 경우 예외를 발생시킨다")
        void throwsExceptionWhenQuantityExceedsStock() {
            String input = "[콜라-100]";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    @Nested
    @DisplayName("Y/N 입력 테스트")
    class YesNoInputTest {

        @AfterEach
        void resetConsole() {
            Console.close();
        }

        @Test
        @DisplayName("추가 구매 진행 여부 확인에서 Y 입력 시 true 반환")
        void returnsTrueForAdditionalPurchase() {
            String input = "Y";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askForAdditionalPurchase();
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("추가 구매 진행 여부 확인에서 N 입력 시 false 반환")
        void returnsFalseForAdditionalPurchase() {
            String input = "N";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askForAdditionalPurchase();
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("멤버십 할인 적용 여부에서 Y 입력 시 true 반환")
        void returnsTrueForMembershipDiscount() {
            String input = "Y";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askForMembershipDiscount();
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("멤버십 할인 적용 여부에서 N 입력 시 false 반환")
        void returnsFalseForMembershipDiscount() {
            String input = "N";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askForMembershipDiscount();
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("추가 무료 상품 확인에서 Y 입력 시 true 반환")
        void returnsTrueForAddFreeItems() {
            String input = "Y";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askToAddFreeItems("콜라", 2);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("추가 무료 상품 확인에서 N 입력 시 false 반환")
        void returnsFalseForAddFreeItems() {
            String input = "N";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askToAddFreeItems("콜라", 2);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("프로모션 할인 미적용 상품 결제 확인에서 Y 입력 시 true 반환")
        void returnsTrueForPayWithoutDiscount() {
            String input = "Y";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askToPayForItemsWithoutDiscount("콜라", 3);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("프로모션 할인 미적용 상품 결제 확인에서 N 입력 시 false 반환")
        void returnsFalseForPayWithoutDiscount() {
            String input = "N";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askToPayForItemsWithoutDiscount("콜라", 3);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("잘못된 Y/N 입력 시 재시도 요구")
        void promptsRetryForInvalidYesNoInput() {
            String input = "X\nY";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            boolean result = inputView.askForMembershipDiscount();
            assertThat(result).isTrue();
        }
    }
}
