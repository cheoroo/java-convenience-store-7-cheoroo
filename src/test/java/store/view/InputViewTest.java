package store.view;

import camp.nextstep.edu.missionutils.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.domain.Purchase;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputViewTest {

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

            InputView inputView = new InputView();
            Purchase purchase = inputView.readPurchase();

            assertThat(purchase.getItems()).hasSize(2);
            assertThat(purchase.getItems().get("콜라")).isEqualTo(3);
        }

        @Test
        @DisplayName("잘못된 형식의 입력일 경우 예외를 발생시킨다")
        void throwsExceptionForInvalidFormat() {
            String input = "콜라-3,사이다-2";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            InputView inputView = new InputView();
            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바르지 않은 형식입니다.");
        }

        @Test
        @DisplayName("수량이 숫자가 아닌 경우 예외를 발생시킨다")
        void throwsExceptionForNonNumericQuantity() {
            String input = "[콜라-abc],[사이다-2]";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            InputView inputView = new InputView();
            assertThatThrownBy(inputView::readPurchase)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 숫자여야 합니다.");
        }
    }
}
