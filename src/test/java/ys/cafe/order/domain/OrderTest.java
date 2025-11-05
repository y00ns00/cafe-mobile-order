package ys.cafe.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ys.cafe.order.domain.vo.Won;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.objectmother.OrderLineMother;
import ys.cafe.order.objectmother.OrderMother;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("Order를 생성할 수 있다")
    void createOrder() {
        // given
        Long memberId = 1L;
        List<OrderLine> orderLines = Arrays.asList(
                OrderLineMother.orderLine(1L, "아메리카노", 2, "4500"),
                OrderLineMother.orderLine(2L, "카페라떼", 1, "5000")
        );

        // when
        Order order = OrderMother.orderWithLines(memberId, orderLines);

        // then
        assertThat(order.getMemberId()).isEqualTo(memberId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);
        assertThat(order.getOrderLines()).hasSize(2);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(Won.of("14000")); // (4500*2) + (5000*1)
    }

    @Test
    @DisplayName("memberId가 null이면 Order 생성 시 예외가 발생한다")
    void createOrderWithNullMemberId() {
        // given
        Long memberId = null;
        List<OrderLine> orderLines = List.of(
                OrderLineMother.orderLine(1L, "아메리카노", 2, "4500")
        );

        // when & then
        assertThatThrownBy(() -> Order.create(memberId, orderLines))
                .isInstanceOf(OrderValidationException.class);
    }

    @Test
    @DisplayName("orderLines가 null이면 Order 생성 시 예외가 발생한다")
    void createOrderWithNullOrderLines() {
        // given
        Long memberId = 1L;
        List<OrderLine> orderLines = null;

        // when & then
        assertThatThrownBy(() -> Order.create(memberId, orderLines))
                .isInstanceOf(OrderValidationException.class);
    }

    @Test
    @DisplayName("orderLines가 비어있으면 Order 생성 시 예외가 발생한다")
    void createOrderWithEmptyOrderLines() {
        // given
        Long memberId = 1L;
        List<OrderLine> orderLines = Collections.emptyList();

        // when & then
        assertThatThrownBy(() -> Order.create(memberId, orderLines))
                .isInstanceOf(OrderValidationException.class);
    }

    @Test
    @DisplayName("totalPrice는 모든 orderLines의 합계로 계산된다")
    void calculateTotalPrice() {
        // given
        Long memberId = 1L;
        List<OrderLine> orderLines = Arrays.asList(
                OrderLineMother.orderLine(1L, "아메리카노", 3, "4500"),  // 13500
                OrderLineMother.orderLine(2L, "카페라떼", 2, "5000"),     // 10000
                OrderLineMother.orderLine(3L, "카푸치노", 1, "5500")      // 5500
        );

        // when
        Order order = OrderMother.orderWithLines(memberId, orderLines);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(Won.of("29000"));
    }

    @Test
    @DisplayName("PAYMENT_WAITING 상태에서 결제 완료 처리를 할 수 있다")
    void completePayment() {
        // given
        Order order = OrderMother.order(1L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);

        // when
        order.completePayment();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    @DisplayName("PAYMENT_WAITING이 아닌 상태에서 결제 완료 처리 시 예외가 발생한다")
    void completePaymentWithInvalidStatus() {
        // given
        Order order = OrderMother.order(1L);
        order.completePayment(); // PREPARING 상태로 변경
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);

        // when & then
        assertThatThrownBy(order::completePayment)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("결제 대기 상태가 아닙니다");
    }

    @Test
    @DisplayName("PAYMENT_WAITING 상태에서 결제 실패 처리를 할 수 있다")
    void failPayment() {
        // given
        Order order = OrderMother.order(1L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);

        // when
        order.failPayment();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("PAYMENT_WAITING이 아닌 상태에서 결제 실패 처리 시 예외가 발생한다")
    void failPaymentWithInvalidStatus() {
        // given
        Order order = OrderMother.order(1L);
        order.completePayment(); // PREPARING 상태로 변경
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);

        // when & then
        assertThatThrownBy(order::failPayment)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("결제 대기 상태가 아닙니다");
    }

    @Test
    @DisplayName("PAYMENT_WAITING 상태에서 주문을 취소할 수 있다")
    void cancelOrderFromPaymentWaiting() {
        // given
        Order order = OrderMother.order(1L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("PREPARING 상태에서 주문을 취소할 수 있다")
    void cancelOrderFromPreparing() {
        // given
        Order order = OrderMother.order(1L);
        order.completePayment(); // PREPARING 상태로 변경
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("PAYMENT_FAILED 상태에서 주문을 취소할 수 있다")
    void cancelOrderFromPaymentFailed() {
        // given
        Order order = OrderMother.order(1L);
        order.failPayment(); // PAYMENT_FAILED 상태로 변경
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("이미 CANCELED 상태인 주문을 취소하면 예외가 발생한다")
    void cancelAlreadyCanceledOrder() {
        // given
        Order order = OrderMother.order(1L);
        order.cancel();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);

        // when & then
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("이미 취소된 주문입니다");
    }

    @Test
    @DisplayName("SERVE 상태인 주문을 취소하면 예외가 발생한다")
    void cancelOrderFromServe() {
        // given
        Order order = OrderMother.orderInServe(1L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.SERVE);

        // when & then
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("취소할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("COMPLETED 상태인 주문을 취소하면 예외가 발생한다")
    void cancelOrderFromCompleted() {
        // given
        Order order = OrderMother.orderInCompleted(1L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);

        // when & then
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("취소할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("PREPARING 상태가 아니면 서빙을 시작할 수 없다")
    void startServingWithInvalidStatus() {
        // given
        Order order = OrderMother.order(1L); // PAYMENT_WAITING 상태

        // when & then
        assertThatThrownBy(order::startServing)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("서빙을 시작할 수 없는 상태입니다");
    }

    @Test
    @DisplayName("SERVE 상태가 아니면 주문을 완료할 수 없다")
    void completeServingWithInvalidStatus() {
        // given
        Order order = OrderMother.orderInPreparing(1L); // PREPARING 상태

        // when & then
        assertThatThrownBy(order::completeServing)
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("주문을 완료할 수 없는 상태입니다");
    }

    @Test
    @DisplayName("Order 생성 시 초기 상태는 PAYMENT_WAITING이다")
    void initialStatusIsPaymentWaiting() {
        // when
        Order order = OrderMother.order(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);
    }

    @Test
    @DisplayName("Order의 모든 정보를 조회할 수 있다")
    void getOrderInfo() {
        // given
        Long memberId = 1L;
        List<OrderLine> orderLines = List.of(
                OrderLineMother.orderLine(1L, "아메리카노", 2, "4500")
        );

        // when
        Order order = OrderMother.orderWithLines(memberId, orderLines);

        // then
        assertThat(order.getMemberId()).isEqualTo(memberId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);
        assertThat(order.getOrderLines()).hasSize(1);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getFormattedOrderDateTime()).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(Won.of("9000"));
    }

    @Test
    @DisplayName("여러 상품을 포함한 Order를 생성할 수 있다")
    void createOrderWithMultipleProducts() {
        // given
        Long memberId = 100L;
        List<OrderLine> orderLines = Arrays.asList(
                OrderLineMother.orderLine(1L, "아메리카노", 2, "4500"),
                OrderLineMother.orderLine(2L, "카페라떼", 1, "5000"),
                OrderLineMother.orderLine(3L, "카푸치노", 3, "5500"),
                OrderLineMother.orderLine(4L, "에스프레소", 1, "3500")
        );

        // when
        Order order = OrderMother.orderWithLines(memberId, orderLines);

        // then
        assertThat(order.getOrderLines()).hasSize(4);
        assertThat(order.getTotalPrice()).isEqualTo(Won.of("34000")); // 9000 + 5000 + 16500 + 3500
    }
}
